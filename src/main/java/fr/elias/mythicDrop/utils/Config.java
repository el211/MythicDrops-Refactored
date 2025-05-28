package fr.elias.mythicDrop.utils;

import fr.elias.mythicDrop.MythicDrop;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Getter
public class Config extends YamlConfiguration {
    private final MythicDrop instance;
    private final File file;

    public Config(File dataFolder , String name) {
        this.instance = MythicDrop.getInstance();
        this.file = new File(dataFolder , name);

        if (!dataFolder.exists()){
            dataFolder.mkdir();
        }

        if (!file.exists()){
            options().copyDefaults(true);
            instance.saveResource(name , false);
        }
        load();
    }


    public Config(String name){
        this(MythicDrop.getInstance().getDataFolder(), name);
    }

    private void load(){
        try {
            super.load(file);
        }catch (FileNotFoundException ex){
            MythicLogger.warn("Configuration file not found: " + file.getName());
        }catch (IOException ex){
            MythicLogger.severe("IO error while loading configuration file: " + file.getName());

        }catch (InvalidConfigurationException ex){
            MythicLogger.severe( "Invalid configuration in file: " + file.getName());
        }
    }

    public void save(){
        try{
            super.save(file);
        }catch (IOException ex){
            MythicLogger.severe("IO error while saving configuration file: " + file.getName());
        }
    }

    public void reload(){
        load();
    }
}