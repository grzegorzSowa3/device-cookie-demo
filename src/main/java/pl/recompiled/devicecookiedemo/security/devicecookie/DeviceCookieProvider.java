package pl.recompiled.devicecookiedemo.security.devicecookie;

interface DeviceCookieProvider {

    String serializeCookie(DeviceCookie deviceCookie);

    DeviceCookie deserializeCookie(String deviceCookie);

}
