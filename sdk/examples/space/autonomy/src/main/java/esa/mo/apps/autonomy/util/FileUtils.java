package esa.mo.apps.autonomy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FileUtils is an utility class for common methods dealing with File class
 */
public class FileUtils {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {}

    public static void loadApplicationProperties(String propertiesPath) {
        File propertiesFile = new File(propertiesPath);
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            LOGGER.info("Loading application properties file " + propertiesFile.getAbsolutePath());

            Properties applicationProperties = new Properties();
            applicationProperties.load(inputStream);

            // String interpolation
            for (String key : applicationProperties.stringPropertyNames()) {
                String value = applicationProperties.getProperty(key);
                String regex = "\\$\\{([^}]*)\\}";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(value);
                while (matcher.find()) {
                    String interpolation = matcher.group(0);
                    String replacement = applicationProperties.getProperty(matcher.group(1), "");
                    value = value.replace(interpolation, replacement);
                    matcher = pattern.matcher(value);
                }
                applicationProperties.setProperty(key, value);
            }

            Properties systemProperties = System.getProperties();
            systemProperties.putAll(applicationProperties);
            System.setProperties(systemProperties);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static File createDirectory(String directoryPath, String usedFor, String defaultPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                String message = String.format("Created directory %s for %s", directory.getAbsolutePath(), usedFor);
                LOGGER.info(message);
                return directory;
            } else {
                String message = String.format("Failed to create directory %s for %s", directory.getAbsolutePath(), usedFor);
                LOGGER.log(Level.SEVERE, message);
            }
        } else if (!directory.canWrite()) {
            String message = String.format("Not allowed to write %s to directory %s", usedFor, directory.getAbsolutePath());
            LOGGER.log(Level.SEVERE, message);
        } else {
            String message = String.format("Outputting %s to directory %s", usedFor, directory.getAbsolutePath());
            LOGGER.info(message);
            return directory;
        }

        // Given path not working - try default path
        directory = new File(defaultPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                String message = String.format("Created default directory %s for %s", directory.getAbsolutePath(), usedFor);
                LOGGER.warning(message);
                return directory;
            } else {
                String message = String.format("Failed to create default directory %s for %s", directory.getAbsolutePath(), usedFor);
                LOGGER.log(Level.SEVERE, message);
            }
        } else if (!directory.canWrite()) {
            String message = String.format("Not allowed to write %s to default directory %s", usedFor, directory.getAbsolutePath());
            LOGGER.log(Level.SEVERE, message);
        } else {
            String message = String.format("Outputting %s to default directory %s", usedFor, directory.getAbsolutePath());
            LOGGER.info(message);
            return directory;
        }
        return null;
    }
}
