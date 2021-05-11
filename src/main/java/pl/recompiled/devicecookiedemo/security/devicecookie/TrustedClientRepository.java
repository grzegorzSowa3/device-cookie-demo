package pl.recompiled.devicecookiedemo.security.devicecookie;

import org.springframework.data.repository.CrudRepository;

interface TrustedClientRepository extends CrudRepository<TrustedClient, String> {

}
