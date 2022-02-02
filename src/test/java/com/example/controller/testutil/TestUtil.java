package com.example.controller.testutil;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;

import static java.lang.Thread.currentThread;
import static java.time.ZoneId.systemDefault;
import static org.mockito.Mockito.doReturn;

public class TestUtil {

    public static InputStream getResourceAsStream(String filePath) {
        return getClassLoader().getResourceAsStream(filePath);
    }

    @SneakyThrows
    public static String readFileContent(String filePath) {
        URI uri = getClassLoader().getResource(filePath).toURI();
        return Files.readString(Path.of(uri));
    }

    private static ClassLoader getClassLoader() {
        return currentThread().getContextClassLoader();
    }

    /**
     * @param clock to mock
     * @param dateTime to return when current time is obtained from java.time.LocalDateTime#now(java.time.Clock)
     *                 or any other similar factory methods.
     */
    public static void mockClockToReturnDateTime(Clock clock, LocalDateTime dateTime) {
        ZoneId zone = systemDefault();
        Clock fixedClock = Clock.fixed(ZonedDateTime.of(dateTime, zone).toInstant(), zone);
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }

    /**
     * see {@link TestUtil#mockClockToReturnDateTime(Clock, LocalDateTime)}
     * @param date
     */
    public static void mockClockToReturnDate(Clock clock, LocalDate date) {
        mockClockToReturnDateTime(clock, date.atStartOfDay());
    }

    private TestUtil() {
    }
}
