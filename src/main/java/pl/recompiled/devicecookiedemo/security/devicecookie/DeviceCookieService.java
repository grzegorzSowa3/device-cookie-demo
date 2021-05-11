package pl.recompiled.devicecookiedemo.security.devicecookie;

public interface DeviceCookieService {

    void validateTrustedClientLogin(String login, String deviceCookie);

    void validateUntrustedClientLogin(String login);

    String generateCookieFor(String login);

    String extractNonce(String deviceCookie);

    void reportTrustedClientLoginFailure(String nonce);

    void reportUntrustedClientLoginFailure(String login);

}
