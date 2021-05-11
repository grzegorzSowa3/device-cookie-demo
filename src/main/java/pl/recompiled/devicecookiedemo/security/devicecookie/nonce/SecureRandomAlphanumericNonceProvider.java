package pl.recompiled.devicecookiedemo.security.devicecookie.nonce;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
class SecureRandomAlphanumericNonceProvider implements NonceProvider {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate(Integer nonceLength) {
        return random.ints()
                .filter(this::isAlphanumeric)
                .limit(nonceLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private boolean isAlphanumeric(int i) {
        return ((i >= 48) && (i <= 57)) || // is between 0-9
                ((i >= 65) && (i <= 90)) || // is between A-Z
                ((i >= 97) && (i <= 122)); // is between a-z
    }

}
