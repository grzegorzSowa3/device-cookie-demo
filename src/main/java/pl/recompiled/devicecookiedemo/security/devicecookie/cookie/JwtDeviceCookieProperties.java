package pl.recompiled.devicecookiedemo.security.devicecookie.cookie;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "device-cookie.jwt")
class JwtDeviceCookieProperties {

    private String issuer;
    private String secret;

}
