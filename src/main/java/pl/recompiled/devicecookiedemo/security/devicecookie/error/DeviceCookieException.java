package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class DeviceCookieException extends RuntimeException {

    public DeviceCookieException() {
    }

    public DeviceCookieException(String message) {
        super(message);
    }

    public DeviceCookieException(Throwable cause) {
        super(cause);
    }

}
