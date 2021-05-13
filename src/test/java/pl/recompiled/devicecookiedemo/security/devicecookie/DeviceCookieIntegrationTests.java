package pl.recompiled.devicecookiedemo.security.devicecookie;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.recompiled.devicecookiedemo.config.GlobalClock;
import pl.recompiled.devicecookiedemo.security.WebSecurityProperties;

import javax.servlet.http.Cookie;
import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieConstants.DEVICE_COOKIE_NAME;

@SpringBootTest
@ActiveProfiles("integration-tests")
@AutoConfigureMockMvc
class DeviceCookieIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebSecurityProperties properties;
    @Autowired
    private DeviceCookieProperties deviceCookieProperties;

    @BeforeEach
    void setup() {
        final Duration timeToReset = Duration.from(deviceCookieProperties.getPenaltyDuration())
                .plus(Duration.from(deviceCookieProperties.getTimeWindow()));
        wait(timeToReset);
    }

    @Test
    void shouldLoginUntrustedClientSuccessfullyAndReturnNewDeviceCookie() throws Exception {
        mockMvc.perform(correctCredentialsRequest(user()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldLoginTrustedClientSuccessfullyAndReturnNewDeviceCookie() throws Exception {
        final Cookie deviceCookie = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        mockMvc.perform(correctCredentialsRequest(user()).cookie(deviceCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldAlwaysReturnNewDeviceCookie() throws Exception {
        final Cookie deviceCookie1 = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        final Cookie deviceCookie2 = mockMvc.perform(correctCredentialsRequest(user()).cookie(deviceCookie1))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        assertNotEquals(deviceCookie1.getValue(), deviceCookie2.getValue());
    }

    @Test
    void shouldNotLoginUntrustedClientIfUntrustedClientsAreLocked() throws Exception {
        lockOutUntrustedClients(user());

        mockMvc.perform(correctCredentialsRequest(user()))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldNotLoginTrustedClientIfItIsLockedAndUntrustedClientsAreLocked() throws Exception {
        final Cookie deviceCookie = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        lockOutTrustedClient(user(), deviceCookie);
        lockOutUntrustedClients(user());

        mockMvc.perform(correctCredentialsRequest(user()).cookie(deviceCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldLoginTrustedClientSuccessfullyIfUntrustedClientsAreLocked() throws Exception {
        final Cookie deviceCookie = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        lockOutUntrustedClients(user());

        mockMvc.perform(correctCredentialsRequest(user()).cookie(deviceCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldLoginTrustedClientSuccessfullyIfOtherTrustedClientIsLocked() throws Exception {
        final Cookie deviceCookie1 = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);
        final Cookie deviceCookie2 = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        lockOutTrustedClient(user(), deviceCookie1);

        mockMvc.perform(correctCredentialsRequest(user()).cookie(deviceCookie2))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(DEVICE_COOKIE_NAME));
    }

    @Test
    void shouldLoginUntrustedClientSuccessfullyIfOneOfTrustedClientsIsLocked() throws Exception {
        final Cookie deviceCookie = mockMvc.perform(correctCredentialsRequest(user()))
                .andReturn().getResponse().getCookie(DEVICE_COOKIE_NAME);

        lockOutTrustedClient(user(), deviceCookie);

        mockMvc.perform(correctCredentialsRequest(user()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(DEVICE_COOKIE_NAME));
    }

    private void lockOutTrustedClient(WebSecurityProperties.User user, Cookie deviceCookie) throws Exception {
        for (int i = 0; i < deviceCookieProperties.getMaxFailures() + 1; i++) {
            mockMvc.perform(incorrectCredentialsRequest(user)
                    .cookie(deviceCookie));
        }
    }

    private void lockOutUntrustedClients(WebSecurityProperties.User user) throws Exception {
        for (int i = 0; i < deviceCookieProperties.getMaxFailures() + 1; i++) {
            mockMvc.perform(incorrectCredentialsRequest(user));
        }
    }

    private MockHttpServletRequestBuilder correctCredentialsRequest(WebSecurityProperties.User user) {
        return post("/login")
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private MockHttpServletRequestBuilder incorrectCredentialsRequest(WebSecurityProperties.User user) {
        return post("/login")
                .param("username", user.getUsername())
                .param("password", user.getPassword() + "abc")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private WebSecurityProperties.User user() {
        return properties.getPredefinedUsers().get(0);
    }

    @SneakyThrows
    private void wait(Duration duration) {
        Clock newClock = Clock.offset(GlobalClock.get(), duration);
        final Field clockField = GlobalClock.class.getDeclaredField("clock");
        clockField.setAccessible(true);
        clockField.set(null, newClock);
    }

}
