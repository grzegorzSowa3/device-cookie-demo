package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class AuthenticationAttemptsLockedException extends DeviceCookieException {

    public AuthenticationAttemptsLockedException() {
    }

    public AuthenticationAttemptsLockedException(String message) {
        super(message);
    }

}
