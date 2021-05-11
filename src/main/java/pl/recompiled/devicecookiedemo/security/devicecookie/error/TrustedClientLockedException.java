package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class TrustedClientLockedException extends DeviceCookieException {

    public TrustedClientLockedException() {
    }

    public TrustedClientLockedException(String message) {
        super(message);
    }

}
