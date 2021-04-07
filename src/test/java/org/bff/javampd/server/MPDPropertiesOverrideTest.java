package org.bff.javampd.server;

import org.bff.javampd.monitor.MonitorProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MPDPropertiesOverrideTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MPDPropertiesOverrideTest.class);

    private static File propertiesFile;
    private MPDProperties properties;

    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        InputStream is = MPDPropertiesOverrideTest.class.getResourceAsStream("/overrides/javampd.properties");
        Properties properties = new Properties();
        properties.load(is);

        URI propPath = MPDPropertiesOverrideTest.class.getResource("/").toURI();

        propertiesFile = new File(new File(propPath).getPath() + "/javampd.properties");

        try {
            propertiesFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error("unable to create file in {}", propPath, e);
            throw e;
        }

        properties.store(new FileWriter(propertiesFile), "");
    }

    @AfterAll
    static void afterAll() {
        propertiesFile.delete();
    }

    @BeforeEach
    void before() {
        properties = new MonitorProperties();
    }

    @Test
    void testAllMonitorOverrides() throws IOException {
        Properties originalProperties = new Properties();
        InputStream is = MPDProperties.class.getResourceAsStream("/mpd.properties");
        originalProperties.load(is);
        originalProperties.keySet()
                .stream()
                .filter(key -> ((String) key).startsWith("monitor"))
                .forEach(key -> assertNotEquals(originalProperties.getProperty((String) key), properties.getPropertyString((String) key)));
    }
}
