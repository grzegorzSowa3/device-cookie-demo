package pl.recompiled.devicecookiedemo.security.devicecookie.nonce;

public interface NonceProvider {

    String generate(Integer nonceLength);

}
