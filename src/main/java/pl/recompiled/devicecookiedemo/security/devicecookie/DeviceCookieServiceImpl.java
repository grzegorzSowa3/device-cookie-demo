package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.recompiled.devicecookiedemo.security.devicecookie.cookie.CookieProvider;
import pl.recompiled.devicecookiedemo.security.devicecookie.cookie.DeviceCookie;
import pl.recompiled.devicecookiedemo.security.devicecookie.nonce.NonceProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class DeviceCookieServiceImpl implements DeviceCookieService {

    private final DeviceCookieProperties properties;
    private final TrustedClientRepository trustedClientRepository;
    private final UntrustedClientRepository untrustedClientRepository;
    private final CookieProvider cookieProvider;
    private final NonceProvider nonceProvider;

    @Override
    public boolean isDeviceCookieValidFor(String login, String deviceCookie) {
        DeviceCookie cookie = cookieProvider.decodeCookie(deviceCookie);
        return cookie.getLogin().equals(login);
    }

    @Override
    public String generateDeviceCookieFor(String login) {
        String nonce = nonceProvider.generate(properties.getNonceLength());
        return cookieProvider.encodeCookie(new DeviceCookie(login, nonce));
    }

    @Override
    public String extractNonce(String deviceCookie) {
        return cookieProvider.decodeCookie(deviceCookie).getNonce();
    }

    @Override
    public boolean isTrustedClientLocked(String nonce) {
        Optional<TrustedClient> trustedClient = trustedClientRepository.findById(nonce);
        return trustedClient.isPresent() && trustedClient.get().isLocked();
    }

    @Override
    public boolean areUntrustedClientsLocked(String login) {
        Optional<UntrustedClient> untrustedClient = untrustedClientRepository.findById(login);
        return untrustedClient.isPresent() && untrustedClient.get().isLocked();
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
