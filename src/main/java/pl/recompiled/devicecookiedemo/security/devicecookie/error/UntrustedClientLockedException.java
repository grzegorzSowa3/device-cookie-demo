package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class UntrustedClientLockedException extends DeviceCookieException {

    public UntrustedClientLockedException() {
    }

    public UntrustedClientLockedException(String message) {
        super(message);
    }

}
