package pl.recompiled.devicecookiedemo.security.devicecookie;

public interface DeviceCookieService {

    void validateCookieFor(String login, String deviceCookie);

    String generateCookieFor(String login);

    void reportTrustedClientLoginFailure(String nonce);

    void reportUntrustedClientLoginFailure(String login);

}
