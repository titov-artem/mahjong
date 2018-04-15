package com.github.mahjong.security.service;

import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.security.service.model.AuthenticationType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Objects;

@Component
public class RequestAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * The plaintext password used to perform {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
     * not found to avoid SEC-2056 (missed user authentication is quicker then existing one).
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";


    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;

    /**
     * The password used to perform {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
     * not found to avoid SEC-2056. This is necessary, because some {@link PasswordEncoder} implementations will short circuit if the
     * password is not in a valid format.
     */
    private final String userNotFoundEncodedPassword;

    @Inject
    public RequestAuthenticationProvider(UserDetailsService userDetailsService,
                                         PasswordEncoder passwordEncoder,
                                         CookieService cookieService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        this.cookieService = cookieService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        if (isAnonymous(userDetails)) {
            logger.info("Anonymous user access");
            return;
        }

        if (!(authentication.getDetails() instanceof AuthenticationType)) {
            authenticateByLoginAndPassword(userDetails, authentication);
            return;
        }

        AuthenticationType authenticationType = (AuthenticationType) authentication.getDetails();
        switch (authenticationType) {
            case LOGIN_PASSWORD:
                logger.info("Authenticating by password");
                authenticateByLoginAndPassword(userDetails, authentication);
                break;
            case COOKIES:
                logger.info("Authenticating by cookie");
                authenticateByCookie(userDetails, authentication);
                break;
            default:
                throw new InternalAuthenticationServiceException("Unsupported authentication token type");
        }
    }

    private boolean isAnonymous(UserDetails userDetails) {
        if (userDetails.getAuthorities().size() != 1) {
            return false;
        }
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        return Objects.equals(MahjongUserRole.ANONYMOUS.name(), authority.getAuthority());
    }

    private void authenticateByLoginAndPassword(UserDetails userDetails,
                                                UsernamePasswordAuthenticationToken authentication) {
        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    private void authenticateByCookie(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        cookieService.validateAuthCookie(new AuthCookies(authentication.getPrincipal().toString(),
                authentication.getCredentials().toString()));
    }

    @Override
    protected UserDetails retrieveUser(String username,
                                       UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (RuntimeException repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }
}
