package net.doodlechaos.dreamvis.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static net.doodlechaos.dreamvis.DreamVis.LOGGER;

public class MyConfig {

    public static String DirectoryPath;
    public static String ProjectName;
    private static final Path CONFIG_PATH = Paths.get("config/myconfig.properties");

    public static void LoadFromFile(){
        Properties properties = new Properties();
        try {
            File configFile = CONFIG_PATH.toFile();
            if (configFile.exists()) {
                properties.load(Files.newInputStream(CONFIG_PATH));
                DirectoryPath = properties.getProperty("DirectoryPath", "H:/MC_Generator_Recordings");
                ProjectName = properties.getProperty("ProjectName", "sessionV1");
                LOGGER.info("Config loaded successfully.");
            } else {
                LOGGER.warn("Config file does not exist. Using default values.");
                DirectoryPath = "H:/MC_Generator_Recordings";
                ProjectName = "sessionV1";
                SaveToFile(); // Create the file with default values if it doesn't exist
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config file: {}", e.getMessage());
        }
    }

    public static void SaveToFile(){
        Properties properties = new Properties();
        properties.setProperty("DirectoryPath", DirectoryPath);
        properties.setProperty("ProjectName", ProjectName);

        try {
            Files.createDirectories(CONFIG_PATH.getParent()); // Ensure the directory exists
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                properties.store(writer, "My Config File");
                LOGGER.info("Config saved successfully.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config file: {}", e.getMessage());
        }
    }
}
