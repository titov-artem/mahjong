package com.github.mahjong.security.service;

import com.github.mahjong.common.json.JsonUtil;
import com.github.mahjong.security.model.AuthToken;
import com.github.mahjong.security.model.MahjongUser;
import com.github.mahjong.security.repo.AuthTokenRepo;
import com.github.mahjong.security.repo.UserRepo;
import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.security.service.model.CookieData;
import com.github.mahjong.security.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class CookieService {
    private static final Logger log = LoggerFactory.getLogger(CookieService.class);

    public static final int COOKIE_EXPIRATION_PERIOD_DAYS = 14;

    private static final int COOKIE_REGENERATION_PERIOD_DAYS = 7;

    private final UserRepo userRepo;
    private final AuthTokenRepo authTokenRepo;

    private final Clock clock;

    private final int maxKeyLength;

    @Inject
    public CookieService(UserRepo userRepo, AuthTokenRepo authTokenRepo, Clock clock) throws GeneralSecurityException {
        this.userRepo = userRepo;
        this.authTokenRepo = authTokenRepo;
        this.clock = clock;
        this.maxKeyLength = CryptoUtil.getMaxKeyLengthBytes();
        log.info("Max permitted key length is {} bytes", maxKeyLength);
    }

    public void validateAuthCookie(AuthCookies cookies) {
        MahjongUser user = userRepo.get(cookies.getLogin())
                .orElseThrow(invalidCookieException());
        CookieData cookieData = decrypt(cookies.getData(), user.getPassword());
        if (!Objects.equals(cookies.getLogin(), cookieData.getLogin())) {
            // Cookies login doesn't match to encrypted login
            throw invalidCookieException().get();
        }
        // Load token that was used to generate this cookie
        AuthToken authToken = authTokenRepo.get(user.getLogin(), cookieData.getToken())
                .orElseThrow(invalidCookieException());
        if (!Objects.equals(authToken.getExpireAt(), cookieData.getExpireAt())) {
            // Cookie expiration doesn't match corresponding token expiration
            throw invalidCookieException().get();
        }
        if (authToken.isExpired(clock)) {
            // Token expired
            throw invalidCookieException().get();
        }
    }

    public AuthCookies refreshAuthCookie(String login) {
        MahjongUser user = userRepo.get(login)
                .orElseThrow(invalidCookieException());
        AuthToken token = getUsableAuthToken(login);
        CookieData cookieData = new CookieData(
                token.getLogin(),
                token.getToken(),
                token.getExpireAt()
        );
        String encryptedCookie = encrypt(cookieData, user.getPassword());
        return new AuthCookies(login, encryptedCookie);
    }

    private AuthToken getUsableAuthToken(String login) {
        return authTokenRepo.getNewest(login)
                .filter(token -> !token.isExpired(clock))
                .filter(token -> token.getExpireAt().isBefore(
                        LocalDateTime.now(clock).plusDays(COOKIE_REGENERATION_PERIOD_DAYS))
                )
                .orElse(authTokenRepo.create(generateAuthToken(login)));
    }

    private AuthToken generateAuthToken(String login) {
        return new AuthToken(
                login,
                UUID.randomUUID().toString(),
                LocalDateTime.now(clock).plusDays(COOKIE_EXPIRATION_PERIOD_DAYS)
        );
    }

    private Supplier<RuntimeException> invalidCookieException() {
        return () -> new BadCredentialsException("Bad credentials");
    }

    private String encrypt(CookieData data, String passPhrase) {
        String jsonData = JsonUtil.writeValue(data).toString();
        try {
            return CryptoUtil.encryptString(jsonData, adoptPassPhrase(passPhrase));
        } catch (GeneralSecurityException e) {
            log.error("Failed to encrypt cookie", e);
            throw new RuntimeException("Failed to encrypt cookie");
        }
    }

    private CookieData decrypt(String data, String passPhrase) {
        try {
            String jsonData = CryptoUtil.decryptString(data, adoptPassPhrase(passPhrase));
            return JsonUtil.readValue(jsonData, CookieData.class);
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to decrypt cookie", e);
            throw new RuntimeException("Failed to decrypt cookie");
        }
    }

    private String adoptPassPhrase(String passPhrase) {
        byte[] passPhraseBytes = passPhrase.getBytes();
        if (passPhraseBytes.length > maxKeyLength) {
            log.warn("Pass phrase too long. Using only first {} bytes", maxKeyLength);
            passPhraseBytes = Arrays.copyOfRange(passPhraseBytes, 0, maxKeyLength);
        }
        log.info("Using passphrase of length {}", passPhraseBytes.length);
        return new String(passPhraseBytes);
    }
}
