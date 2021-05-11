package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class CookieInvalidException extends DeviceCookieException {

    public CookieInvalidException() {
    }

    public CookieInvalidException(String message) {
        super(message);
    }

    public CookieInvalidException(Throwable cause) {
        super(cause);
    }

}
