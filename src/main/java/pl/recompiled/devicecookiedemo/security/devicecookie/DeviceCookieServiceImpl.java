package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.CookieLoginMismatchException;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.TrustedClientLockedException;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.UntrustedClientLockedException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class DeviceCookieServiceImpl implements DeviceCookieService {

    private final DeviceCookieProperties properties;
    private final TrustedClientRepository trustedClientRepository;
    private final UntrustedClientRepository untrustedClientRepository;
    private final DeviceCookieSerializer deviceCookieSerializer;
    private final NonceGenerator nonceGenerator;

    @Override
    public void validateTrustedClientLogin(String login, String deviceCookie) {
        DeviceCookie cookie = deviceCookieSerializer.deserializeCookie(deviceCookie);
        if (!cookie.getLogin().equals(login)) {
            throw new CookieLoginMismatchException(
                    String.format("Attempted login: %s different from login in device cookie: %s", login, cookie.getLogin()));
        }
        Optional<TrustedClient> trustedClient = trustedClientRepository.findById(cookie.getNonce());
        if (trustedClient.isPresent() && trustedClient.get().isLocked()) {
            throw new TrustedClientLockedException(
                    String.format("Trusted client with nonce: %s is locked", cookie.getNonce()));
        }
    }

    @Override
    public void validateUntrustedClientLogin(String login) {
        Optional<UntrustedClient> untrustedClient = untrustedClientRepository.findById(login);
        if (untrustedClient.isPresent() && untrustedClient.get().isLocked()) {
            throw new UntrustedClientLockedException(
                    String.format("Untrusted clients for login: %s are locked", login));
        }
    }

    @Override
    public String generateCookieFor(String login) {
        String nonce = nonceGenerator.generate();
        LocalDateTime validUntil = LocalDateTime.now().plus(properties.getCookieValidityDuration());
        return deviceCookieSerializer.serializeCookie(new DeviceCookie(login, nonce, validUntil));
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
