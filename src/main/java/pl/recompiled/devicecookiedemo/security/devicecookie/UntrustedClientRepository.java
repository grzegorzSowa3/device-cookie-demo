package pl.recompiled.devicecookiedemo.security.devicecookie;

import org.springframework.data.repository.CrudRepository;

interface UntrustedClientRepository extends CrudRepository<UntrustedClient, String> {

}
