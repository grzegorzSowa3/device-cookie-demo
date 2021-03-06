package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.recompiled.devicecookiedemo.config.GlobalClock;

import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
class GenericClient {

    @Convert(converter = ListOfLongsConverter.class)
    private List<Long> failedLoginAttempts;
    private LocalDateTime lockedUntil;

    static GenericClient newInstance() {
        return new GenericClient(new ArrayList<>(), null);
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now(GlobalClock.get()));
    }

    public void registerFailedLoginAttempt(DeviceCookieProperties properties) {
        failedLoginAttempts.add(LocalDateTime.now(GlobalClock.get()).atZone(ZoneId.systemDefault()).toEpochSecond());
        trimFailedLoginAttempts(properties);
        if (isMaxLoginAttemptsExceeded(properties)) {
            lock(properties);
        }
    }

    private void trimFailedLoginAttempts(DeviceCookieProperties properties) {
        Long oldestValidTimestamp = LocalDateTime.now(GlobalClock.get()).minus(properties.getTimeWindow())
                .atZone(ZoneId.systemDefault()).toEpochSecond();
        this.failedLoginAttempts.removeIf(timestamp -> timestamp < oldestValidTimestamp);
    }

    private boolean isMaxLoginAttemptsExceeded(DeviceCookieProperties properties) {
        return failedLoginAttempts.size() > properties.getMaxFailures();
    }

    private void lock(DeviceCookieProperties properties) {
        this.failedLoginAttempts = new ArrayList<>();
        this.lockedUntil = LocalDateTime.now(GlobalClock.get()).plus(properties.getPenaltyDuration());
    }

}
