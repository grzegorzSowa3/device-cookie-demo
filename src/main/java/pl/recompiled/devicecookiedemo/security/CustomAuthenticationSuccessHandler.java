package pl.recompiled.devicecookiedemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final DeviceCookieService deviceCookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.addCookie(new Cookie("device", deviceCookieService.generateDeviceCookieFor(authentication.getName())));
        response.setStatus(OK.value());
    }

}
