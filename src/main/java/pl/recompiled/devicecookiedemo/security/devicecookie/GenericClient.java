package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
class GenericClient {

    private List<Long> failedLoginAttempts;
    private LocalDateTime lockedUntil;

    static GenericClient newInstance() {
        return new GenericClient(new ArrayList<>(), null);
    }

    public boolean isLocked() {
        return lockedUntil == null || lockedUntil.isBefore(LocalDateTime.now());
    }

    public void registerFailedLoginAttempt(DeviceCookieProperties properties) {
        failedLoginAttempts.add(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        trimFailedLoginAttempts(properties);
        if (isMaxLoginAttemptsExceeded(properties)) {
            lock(properties);
        }
    }

    private void trimFailedLoginAttempts(DeviceCookieProperties properties) {
        Long oldestValidTimestamp = LocalDateTime.now().minus(properties.getTimeWindow())
                .atZone(ZoneId.systemDefault()).toEpochSecond();
        this.failedLoginAttempts.removeIf(timestamp -> timestamp < oldestValidTimestamp);
    }

    private boolean isMaxLoginAttemptsExceeded(DeviceCookieProperties properties) {
        return failedLoginAttempts.size() > properties.getMaxFailures();
    }

    private void lock(DeviceCookieProperties properties) {
        this.failedLoginAttempts = new ArrayList<>();
        this.lockedUntil = LocalDateTime.now().plus(properties.getPenaltyDuration());
    }

}
