package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
class DeviceCookie {

    private String login;
    private String nonce;
    private LocalDateTime validUntil;

}
