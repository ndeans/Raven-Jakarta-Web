package us.deans.raven;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

@Named
@ApplicationScoped
public class ConfigBean implements Serializable {

    private static final String CONFIG_PATH = "/opt/raven-web/raven-web.properties";

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final String DEFAULT_DARK_MODE = "light";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private int pageSize = DEFAULT_PAGE_SIZE;
    private String darkModeDefault = DEFAULT_DARK_MODE;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_PATH)) {
            props.load(in);
        } catch (IOException ex) {
            logger.warn("Could not read {} — falling back to defaults (PAGE_SIZE={}, DARK_MODE={})",
                    CONFIG_PATH, DEFAULT_PAGE_SIZE, DEFAULT_DARK_MODE);
            return;
        }

        pageSize = parseIntOrDefault(props.getProperty("PAGE_SIZE"), DEFAULT_PAGE_SIZE);
        darkModeDefault = props.getProperty("DARK_MODE", DEFAULT_DARK_MODE);
    }

    private int parseIntOrDefault(String value, int fallback) {
        if (value == null) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            logger.warn("Invalid PAGE_SIZE '{}' in {} — using default {}", value, CONFIG_PATH, fallback);
            return fallback;
        }
    }

    public int getPageSize() { return pageSize; }

    public String getDarkModeDefault() { return darkModeDefault; }
}
