package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import static lombok.AccessLevel.PRIVATE;

@Entity
@RequiredArgsConstructor(access = PRIVATE)
class TrustedClient implements Persistable<String> {

    @Id
    @Getter
    private final String nonce;

    @Transient
    @Getter
    private boolean isNew;

    @Embedded
    @Delegate
    private final GenericClient client;

    static TrustedClient newInstance(String nonce) {
        var trustedClient = new TrustedClient(nonce, GenericClient.newInstance());
        trustedClient.isNew = true;
        return trustedClient;
    }

    @Override
    public String getId() {
        return nonce;
    }

}
