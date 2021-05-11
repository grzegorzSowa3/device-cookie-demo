package pl.recompiled.devicecookiedemo.security.devicecookie;

interface NonceProvider {

    String generate(Integer nonceLength);

}
