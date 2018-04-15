package com.github.mahjong.common.security.api.service;

import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.common.security.api.model.MahjongUserDetails;
import com.github.mahjong.common.security.api.rest.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

@Service
public class RequestAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(RequestAuthenticationProvider.class);

    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
     * Remote authentication service interface.
     */
    private final UserController userController;

    @Inject
    public RequestAuthenticationProvider(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                "Only UsernamePasswordAuthenticationToken is supported");

        // Determine username
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        UserDetails user;
        try {
            user = authenticateUser(username, (UsernamePasswordAuthenticationToken) authentication);
        } catch (UsernameNotFoundException notFound) {
            logger.debug("User '" + username + "' not found");

            throw new BadCredentialsException("Bad credentials");
        }
        postAuthenticationChecks.check(user);

        Object principalToReturn = user;

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    private UserDetails authenticateUser(String username,
                                         UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String credentials = authentication.getCredentials().toString();
        Response response = userController.authenticate(
                new Cookie(AuthCookies.LOGIN_COOKIE_NAME, username),
                new Cookie(AuthCookies.DATA_COOKIE_NAME, credentials),
                null,
                null
        );
        if (response.getStatus() == 204) {
            return new MahjongUserDetails(username, credentials, MahjongUserRole.USER);
        }
        if (response.getStatus() == 403) {
            return new MahjongUserDetails(username, credentials, MahjongUserRole.ANONYMOUS);
        }
        throw new InternalAuthenticationServiceException("Failed to connect to remote authentication service");
    }

    /**
     * Creates a successful {@link Authentication} object.<p>Protected so subclasses can override.</p>
     * <p>Subclasses will usually store the original credentials the user supplied (not salted or encoded
     * passwords) in the returned <code>Authentication</code> object.</p>
     *
     * @param principal      that should be the principal in the returned object
     * @param authentication that was presented to the provider for validation
     * @param user           that was loaded by the implementation
     * @return the successful authentication token
     */
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
                                                         UserDetails user) {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
                authentication.getCredentials(), authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("User account is locked");

                throw new LockedException("User account is locked");
            }

            if (!user.isEnabled()) {
                logger.debug("User account is disabled");

                throw new DisabledException("User is disabled");
            }

            if (!user.isAccountNonExpired()) {
                logger.debug("User account is expired");

                throw new AccountExpiredException("User account has expired");
            }
            if (!user.isCredentialsNonExpired()) {
                logger.debug("User account credentials have expired");

                throw new CredentialsExpiredException("User credentials have expired");
            }
        }
    }
}
