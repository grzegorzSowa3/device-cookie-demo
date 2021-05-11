package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class DeviceCookie {

    private String login;
    private String nonce;

}
