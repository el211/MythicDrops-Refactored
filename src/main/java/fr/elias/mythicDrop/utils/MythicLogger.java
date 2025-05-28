package fr.elias.mythicDrop.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MythicLogger {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private static final String PREFIX = "[MYTHIC DROP]";

    private static void log(Level level, String content) {
        LOGGER.log(level, PREFIX + " " + content);
    }

    public static void info(String content) {
        log(Level.INFO, content);
    }

    public static void warn(String content) {
        log(Level.WARNING, content);
    }

    public static void severe(String content) {
        log(Level.SEVERE, content);
    }

}