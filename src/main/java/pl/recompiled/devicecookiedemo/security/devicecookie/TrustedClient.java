package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor(access = PRIVATE)
class TrustedClient implements Persistable<String> {

    @Id
    @Getter
    private String nonce;

    @Transient
    @Getter
    private boolean isNew;

    @Embedded
    @Delegate
    private GenericClient client;

    static TrustedClient newInstance(String nonce) {
        var trustedClient = new TrustedClient();
        trustedClient.nonce = nonce;
        trustedClient.client = GenericClient.newInstance();
        trustedClient.isNew = true;
        return trustedClient;
    }

    @Override
    public String getId() {
        return nonce;
    }

}
