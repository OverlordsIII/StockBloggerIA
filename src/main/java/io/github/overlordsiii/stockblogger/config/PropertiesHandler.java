package io.github.overlordsiii.stockblogger.config;

import io.github.overlordsiii.stockblogger.StockBlogger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import javax.swing.text.html.HTML;

public class PropertiesHandler {

    private final Path propertiesPath;

    private final Map<String, String> configValues;

    private final boolean nonNull;

    public static Path CONFIG_HOME_DIRECTORY = Paths.get("src", "main", "resources").resolve("Stock Blogger Config");

    public static final Path HTML_FILE_DIRECTORY;

    static {
        System.out.println("Are you running from a production environment (one with a .jar file)? If so, respond true.");

        boolean prodEnv = StockBlogger.SCANNER.nextBoolean();

        if (prodEnv) {
            try {
                CONFIG_HOME_DIRECTORY = new File(PropertiesHandler.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).toPath();
            } catch (URISyntaxException e) {
                System.out.println("Error while finding config home directory!");
                e.printStackTrace();
            }
        }


        if (!Files.exists(CONFIG_HOME_DIRECTORY)) {
            try {
                Files.createDirectory(CONFIG_HOME_DIRECTORY);
            } catch (IOException e) {
                System.out.println("Error while creating config home directory at: \"" + CONFIG_HOME_DIRECTORY + "\"");
                e.printStackTrace();
            }
        }

        HTML_FILE_DIRECTORY = CONFIG_HOME_DIRECTORY.getParent().resolve("html");
    }

    private PropertiesHandler(String filename, Map<String, String> configValues, boolean nonNull) {
        this.nonNull = nonNull;
        this.propertiesPath = CONFIG_HOME_DIRECTORY.resolve(filename);
        this.configValues = configValues;
    }

    public void initialize() {
        try {
            load();
            save();
            nonNullCheck();
        } catch (IOException e) {
            System.out.println("Error while initializing Properties Config for file " + "\"" + propertiesPath + "\"" + "!");
            e.printStackTrace();
        }



    }

    public void load() throws IOException {

        if (!Files.exists(propertiesPath)) {
            // return bc the file has not been saved yet
            return;
        }

        Properties properties = new Properties();

        properties.load(Files.newInputStream(propertiesPath));

        properties.forEach((o, o2) -> configValues.put(o.toString(), o2.toString()));

    }

    public void save() throws IOException {

        if (!Files.exists(propertiesPath.getParent())) {
            throw new RuntimeException("Could not find directory \"" + propertiesPath.getParent() + "\"!");
        }

        Properties properties = new Properties();

        properties.putAll(configValues);

        properties.store(Files.newOutputStream(propertiesPath), "This stores the configuration properties for Stock Blogger");

    }

    public static Builder builder() {
        return new Builder();
    }

    public void setConfigOption(String option, String newValue) {
        configValues.replace(option, newValue);
    }

    public void reload() {
        try {
            save();
            load();
            nonNullCheck();
        } catch (IOException e) {
            System.out.println("Error while initializing Properties Config for file " + "\"" + propertiesPath + "\"" + "!");
            e.printStackTrace();
        }
    }

    private void nonNullCheck() {
        configValues.forEach((key, value) -> {
            if ((value == null || value.isEmpty() || value.equals("null")) && nonNull) {
                throw new NullPointerException("Key \"" + key + "\" was null for Properties Handler \"" + propertiesPath.getFileName() + "\"!");
            }
        });
    }

    public <T> T getConfigOption(String key, Function<String, T> parser) {
        return parser.apply(getConfigOption(key));
    }

    public String getConfigOption(String key) {
        String value = configValues.get(key);

        if (!value.endsWith(".html")) {
            return value;
        }

        Path htmlPath = HTML_FILE_DIRECTORY.resolve(value);

        if (!Files.exists(htmlPath)) {
            return value;
        }

        String file;

        try {
            file = Files.readString(htmlPath);
        } catch (IOException e) {
            System.out.println("Error while reading string for html file on path \"" + htmlPath + "\"!");
            e.printStackTrace();
            return null;
        }

        return file;
    }

    public boolean hasConfigOption(String key) {
        return configValues.get(key) != null && !configValues.get(key).isEmpty();
    }

    public boolean containsKey(String key) {
        return configValues.containsKey(key);
    }


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append("\"")
                .append(propertiesPath.getFileName())
                .append("\"")
                .append(": ")
                .append("{\n");

        configValues.forEach((s, s2) -> {
            builder
                    .append("\"")
                    .append(s)
                    .append("\"")
                    .append(": ")
                    .append("\"")
                    .append(s2)
                    .append("\"")
                    .append("\n");
        });

        builder.append("}");

        return builder.toString();
    }

    public void validateNonNull() {
        if (this.nonNull) {
            for (Map.Entry<String, String> entry : this.configValues.entrySet()) {
                String s = entry.getKey();
                String s2 = entry.getValue();
                if (s2 == null || s2.isEmpty() || s2.equals("null")) {
                    throw new NullPointerException("Property \"" + s + "\" was null/empty! Make sure you go to \"" + this.propertiesPath + "\" and ensure that it is not null!\n If this is due to an API key not being entered, please enter the API key from the provider to test it!");
                }
            }
        }
    }

    public static class Builder {

        private final Map<String, String> configValues = new HashMap<>();
        private String filename;

        private boolean nonNull = false;

        private Builder() {
        }

        public Builder requireNonNull() {
            this.nonNull = true;
            return this;
        }

        public Builder addConfigOption(String key, String defaultValue) {
            configValues.put(key, defaultValue);
            return this;
        }

        public Builder addConfigOption(String key, Object value) {
            return addConfigOption(key, Objects.toString(value));
        }

        public Builder setFileName(String fileName) {
            if (!fileName.endsWith(".properties")) {
                fileName += ".properties";
            }

            this.filename = fileName;

            return this;
        }

        public PropertiesHandler build() {
            PropertiesHandler propertiesHandler = new PropertiesHandler(filename, configValues, nonNull);
            propertiesHandler.initialize();
            System.out.println("Properties Handler with file name \"" + filename + "\" created on path \"" + propertiesHandler.propertiesPath + "\"");
            return propertiesHandler;
        }
    }
}