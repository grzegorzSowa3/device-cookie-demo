package pl.recompiled.devicecookiedemo.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class WebSecurityProperties {

    private List<User> predefinedUsers;

    @Data
    public static class User {
        private String username;
        private String password;
        private List<String> roles;
    }

}
