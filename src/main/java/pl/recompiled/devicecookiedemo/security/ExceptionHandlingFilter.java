package pl.recompiled.devicecookiedemo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.DeviceCookieException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
class ExceptionHandlingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (DeviceCookieException e) {
            if (e.getMessage() != null) {
                log.warn(String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
            } else {
                log.warn(e.getClass().getSimpleName(), e);
            }
            ((HttpServletResponse) response).setStatus(UNAUTHORIZED.value());
        }
    }

}
