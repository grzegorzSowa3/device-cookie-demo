package pl.recompiled.devicecookiedemo.security.devicecookie;

interface DeviceCookieProvider {

    String encodeCookie(DeviceCookie deviceCookie);

    DeviceCookie decodeCookie(String deviceCookie);

}
