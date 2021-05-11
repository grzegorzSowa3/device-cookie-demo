package pl.recompiled.devicecookiedemo.security.devicecookie;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import pl.recompiled.devicecookiedemo.security.devicecookie.error.CookieInvalidException;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
class JwtDeviceCookieProvider implements DeviceCookieProvider {

    private final JwtDeviceCookieProperties properties;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    private final JWSHeader header;

    JwtDeviceCookieProvider(JwtDeviceCookieProperties properties) throws JOSEException {
        this.properties = properties;
        this.signer = new MACSigner(properties.getSecret());
        this.verifier = new MACVerifier(properties.getSecret());
        this.header = new JWSHeader(JWSAlgorithm.HS256);
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
            verifyCookieSignature(jwt);
            claims = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new CookieInvalidException(e);
        }
        return new DeviceCookie(claims.getSubject(), claims.getJWTID());
    }

    private void verifyCookieSignature(SignedJWT jwt) {
        try {
            if (!jwt.verify(this.verifier)) {
                throw new CookieInvalidException("Device cookie signature is invalid");
            }
        } catch (JOSEException e) {
            throw new CookieInvalidException(e);
        }
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Date toDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(instant);
    }

}
