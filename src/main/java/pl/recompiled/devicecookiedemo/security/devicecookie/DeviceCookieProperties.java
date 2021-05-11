package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "device-cookie")
class DeviceCookieProperties {

    @Setter
    private String timeWindow;
    @Setter
    private String penaltyDuration;
    @Setter
    @Getter
    private Integer maxFailures;
    @Setter
    @Getter
    private Integer nonceLength;

    private TemporalAmount timeWindowParsed;
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
