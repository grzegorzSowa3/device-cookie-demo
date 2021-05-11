package pl.recompiled.devicecookiedemo.security.devicecookie;

import java.time.LocalDateTime;

interface DeviceCookieProvider {

    String createCookie(String login, String nonce, LocalDateTime validUntil);

}
