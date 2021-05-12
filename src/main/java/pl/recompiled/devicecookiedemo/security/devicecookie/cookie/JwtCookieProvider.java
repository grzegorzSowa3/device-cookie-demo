package pl.recompiled.devicecookiedemo.security.devicecookie.cookie;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.CookieInvalidException;

import java.text.ParseException;

@Slf4j
@Component
class JwtCookieProvider implements CookieProvider {

    private final JwtCookieProperties properties;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    private final JWSHeader header;

    JwtCookieProvider(JwtCookieProperties properties) throws JOSEException {
        this.properties = properties;
        this.signer = new MACSigner(properties.getSecret());
        this.verifier = new MACVerifier(properties.getSecret());
        this.header = new JWSHeader(JWSAlgorithm.HS256);
    }

    @Override
    public boolean isCookieValid(String deviceCookie) {
        try {
            SignedJWT jwt = SignedJWT.parse(deviceCookie);
            if (!jwt.verify(this.verifier)) {
                log.warn("Device cookie signature is invalid");
                return false;
            }
        } catch (JOSEException | ParseException e) {
            log.warn(e.getClass().getSimpleName(), e);
            return false;
        }
        return true;
    }

    @Override
    @SneakyThrows(JOSEException.class)
    public String encodeCookie(DeviceCookie deviceCookie) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.getIssuer())
                .jwtID(deviceCookie.getNonce())
                .subject(deviceCookie.getLogin())
                .build();
        SignedJWT jwt = new SignedJWT(this.header, claims);
        jwt.sign(this.signer);
        return jwt.serialize();
    }

    @Override
    public DeviceCookie decodeCookie(String deviceCookie) {
        final SignedJWT jwt;
        final JWTClaimsSet claims;
        try {
            jwt = SignedJWT.parse(deviceCookie);
            claims = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new CookieInvalidException(e);
        }
        return new DeviceCookie(claims.getSubject(), claims.getJWTID());
    }

}
