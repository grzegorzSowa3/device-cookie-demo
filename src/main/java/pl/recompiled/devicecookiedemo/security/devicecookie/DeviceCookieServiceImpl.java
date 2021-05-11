package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class DeviceCookieServiceImpl implements DeviceCookieService {

    private final DeviceCookieProperties properties;
    private final TrustedClientRepository trustedClientRepository;
    private final UntrustedClientRepository untrustedClientRepository;
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
        TrustedClient trustedClient = trustedClientRepository.findById(nonce)
                .orElse(TrustedClient.newInstance(nonce));
        trustedClient.registerFailedLoginAttempt(properties);
        trustedClientRepository.save(trustedClient);
    }

    @Override
    public void reportUntrustedClientLoginFailure(String login) {
        UntrustedClient untrustedClient = untrustedClientRepository.findById(login)
                .orElse(UntrustedClient.newInstance(login));
        untrustedClient.registerFailedLoginAttempt(properties);
        untrustedClientRepository.save(untrustedClient);
    }

}
