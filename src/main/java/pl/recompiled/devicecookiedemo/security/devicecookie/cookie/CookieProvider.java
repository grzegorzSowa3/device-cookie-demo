package pl.recompiled.devicecookiedemo.security.devicecookie.cookie;

public interface CookieProvider {

    String encodeCookie(DeviceCookie deviceCookie);

    DeviceCookie decodeCookie(String deviceCookie);

}
