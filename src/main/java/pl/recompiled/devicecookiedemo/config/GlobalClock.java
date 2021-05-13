package pl.recompiled.devicecookiedemo.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDateTime;

@Configuration
public class GlobalClock {

    private static Clock clock;

    @PostConstruct
    public void init() {
        clock = Clock.systemDefaultZone();
    }

    public static Clock get() {
        return clock;
    }

}
