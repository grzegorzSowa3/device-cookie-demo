package pl.recompiled.devicecookiedemo.security.devicecookie;

interface DeviceCookieSerializer {

    String serializeCookie(DeviceCookie deviceCookie);

    DeviceCookie deserializeCookie(String deviceCookie);

}
