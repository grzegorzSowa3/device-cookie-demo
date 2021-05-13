package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

@Setter
@Configuration
@ConfigurationProperties(prefix = "device-cookie")
class DeviceCookieProperties {

    private String timeWindow;
    private String penaltyDuration;
    @Getter
    private Integer maxFailures;
    @Getter
    private Integer nonceLength;

    @Setter(AccessLevel.PRIVATE)
    private TemporalAmount timeWindowParsed;
    @Setter(AccessLevel.PRIVATE)
    private TemporalAmount penaltyDurationParsed;

    TemporalAmount getTimeWindow() {
        if (Objects.isNull(timeWindowParsed)) {
            timeWindowParsed = parseTemporalAmount(timeWindow);
        }
        return timeWindowParsed;
    }

    TemporalAmount getPenaltyDuration() {
        if (Objects.isNull(penaltyDurationParsed)) {
            penaltyDurationParsed = parseTemporalAmount(penaltyDuration);
        }
        return penaltyDurationParsed;
    }

    private TemporalAmount parseTemporalAmount(String str) {
        try {
            return Duration.parse(str);
        } catch (DateTimeParseException e) {
            return Period.parse(str);
        }
    }

}
