package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DeviceCookieServiceImpl implements DeviceCookieService {

    private final DeviceCookieProperties properties;

    @Override
    public boolean isCookieValidFor(String login, String deviceCookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateCookieFor(String login) {
        return "test";
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
