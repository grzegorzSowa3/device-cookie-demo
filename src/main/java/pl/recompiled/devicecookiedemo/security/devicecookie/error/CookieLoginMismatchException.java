package pl.recompiled.devicecookiedemo.security.devicecookie.error;

public class CookieLoginMismatchException extends DeviceCookieException {

    public CookieLoginMismatchException() {
    }

    public CookieLoginMismatchException(String message) {
        super(message);
    }

}
