package pl.recompiled.devicecookiedemo.security.devicecookie.cookie;

public interface CookieProvider {

    boolean isCookieValid(String deviceCookie);

    String encodeCookie(DeviceCookie deviceCookie);

    DeviceCookie decodeCookie(String deviceCookie);

}
