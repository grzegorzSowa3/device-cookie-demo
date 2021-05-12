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
class UntrustedClient implements Persistable<String> {

    @Id
    @Getter
    private String login;

    @Transient
    @Getter
    private boolean isNew;

    @Embedded
    @Delegate
    private GenericClient client;

    static UntrustedClient newInstance(String login) {
        var untrustedClient = new UntrustedClient();
        untrustedClient.login = login;
        untrustedClient.client = GenericClient.newInstance();
        untrustedClient.isNew = true;
        return untrustedClient;
    }

    @Override
    public String getId() {
        return login;
    }

}
