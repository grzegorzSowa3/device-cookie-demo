package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class DeviceCookieServiceImpl implements DeviceCookieService {

    private final DeviceCookieProperties properties;
    private final DeviceCookieProvider deviceCookieProvider;
    private final NonceProvider nonceProvider;

    @Override
    public void validateCookieFor(String login, String deviceCookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateCookieFor(String login) {
        String nonce = nonceProvider.generateNonce();
        LocalDateTime validUntil = LocalDateTime.now().plus(properties.getCookieValidityDuration());
        return deviceCookieProvider.createCookie(login, nonce, validUntil);
    }

    @Override
    public void reportTrustedClientLoginFailure(String nonce) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reportUntrustedClientLoginFailure(String login) {
        throw new UnsupportedOperationException();
    }
}
