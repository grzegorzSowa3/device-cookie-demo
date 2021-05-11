package pl.recompiled.devicecookiedemo.security.devicecookie;

public interface DeviceCookieService {

    boolean isDeviceCookieValidFor(String login, String deviceCookie);

    String generateDeviceCookieFor(String login);

    String extractNonce(String deviceCookie);

    boolean isTrustedClientLocked(String nonce);

    boolean areUntrustedClientsLocked(String login);

    void reportTrustedClientLoginFailure(String nonce);

    void reportUntrustedClientLoginFailure(String login);

}
