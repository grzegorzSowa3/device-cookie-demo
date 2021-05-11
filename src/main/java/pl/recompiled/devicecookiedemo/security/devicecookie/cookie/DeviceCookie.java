package pl.recompiled.devicecookiedemo.security.devicecookie.cookie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceCookie {

    private String login;
    private String nonce;

}
