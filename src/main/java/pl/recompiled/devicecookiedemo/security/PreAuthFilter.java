package pl.recompiled.devicecookiedemo.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.filter.GenericFilterBean;
import pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieService;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.AuthenticationAttemptsLockedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieConstants.*;

@RequiredArgsConstructor
class PreAuthFilter extends GenericFilterBean {

    private final DeviceCookieService deviceCookieService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String username = httpRequest.getParameter("username");
        if (username == null || username.isBlank()
                || !httpRequest.getRequestURI().contains("/login")) {
            // if it is not a login request, skip this filter
            filterChain.doFilter(request, response);
            return;
        }

        final Optional<String> deviceCookie = Arrays.stream(ArrayUtils.nullToEmpty(httpRequest.getCookies(), Cookie[].class))
                .filter(cookie -> cookie.getName().equals(DEVICE_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();

        // if request has a valid device cookie
        if (deviceCookie.isPresent() && deviceCookieService.isDeviceCookieValidFor(username, deviceCookie.get())) {
            final String nonce = deviceCookieService.extractNonce(deviceCookie.get());

            // if trusted client identified by cookie nonce is not locked
            if (!deviceCookieService.isTrustedClientLocked(nonce)) {
                // proceed with authentication as trusted client
                proceedAsTrustedClient(request, response, filterChain, nonce);
                return;
            }
        }
        // else treat as untrusted client

        // if untrusted clients are not locked from that account
        if (!deviceCookieService.areUntrustedClientsLocked(username)) {
            // proceed with authentication as untrusted client
            proceedAsUntrustedClient(request, response, filterChain);
            return;
        }

        // else throw exception, blocking authentication
        request.setAttribute(AUTH_ALLOWED_ATTR_NAME, false);
        throw new AuthenticationAttemptsLockedException(
                String.format("Authentication attempts for login: %s are locked", username));

    }

    private void proceedAsTrustedClient(ServletRequest request, ServletResponse response, FilterChain filterChain,
                                        String nonce) throws IOException, ServletException {
        request.setAttribute(AUTH_ALLOWED_ATTR_NAME, true);
        request.setAttribute(CLIENT_TRUSTED_ATTR_NAME, true);
        request.setAttribute(NONCE_ATTR_NAME, nonce);
        filterChain.doFilter(request, response);
    }

    private void proceedAsUntrustedClient(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute(AUTH_ALLOWED_ATTR_NAME, true);
        request.setAttribute(CLIENT_TRUSTED_ATTR_NAME, false);
        filterChain.doFilter(request, response);
    }

}
