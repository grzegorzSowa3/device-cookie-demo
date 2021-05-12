package pl.recompiled.devicecookiedemo.security.devicecookie.nonce;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
class SecureRandomAlphanumericNonceProvider implements NonceProvider {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate(Integer nonceLength) {
        return RandomStringUtils.random(nonceLength, 0, 0, true, true, null, this.random);
    }

}
