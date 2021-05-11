package pl.recompiled.devicecookiedemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieConstants.*;

@RequiredArgsConstructor
class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final DeviceCookieService deviceCookieService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        if ((Boolean) request.getAttribute(AUTH_ALLOWED_ATTR_NAME)) {
            if ((Boolean) request.getAttribute(CLIENT_TRUSTED_ATTR_NAME)) {
                deviceCookieService.reportTrustedClientLoginFailure((String) request.getAttribute(NONCE_ATTR_NAME));
            } else {
                deviceCookieService.reportUntrustedClientLoginFailure(request.getParameter("username"));
            }
        }
        response.setStatus(UNAUTHORIZED.value());
    }

}
