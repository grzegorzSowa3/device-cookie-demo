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
class UntrustedClient implements Persistable<String> {

    @Id
    @Getter
    private final String login;

    @Transient
    @Getter
    private boolean isNew;

    @Embedded
    @Delegate
    private final GenericClient client;

    static UntrustedClient newInstance(String nonce) {
        var trustedClient = new UntrustedClient(nonce, GenericClient.newInstance());
        trustedClient.isNew = true;
        return trustedClient;
    }

    @Override
    public String getId() {
        return login;
    }

}
