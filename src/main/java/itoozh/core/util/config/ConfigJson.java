package itoozh.core.util.config;

import itoozh.core.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigJson {

    private final File file;
    private final Main instance;
    private final String name;
    private final Map<String, Object> values;

    public ConfigJson(Main plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        this.values = new ConcurrentHashMap<>();
        this.instance = plugin;
        this.name = name;
        this.load();
    }

    public void save() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(this.file.toPath()), StandardCharsets.UTF_8);
            this.instance.getGson().toJson(this.values, outputStreamWriter);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void load() {
        try {
            if (!this.file.exists()) {
                this.instance.saveResource(this.name, false);
            }
            FileReader fileReader = new FileReader(this.file);
            Map<String, Object> strings = (Map<String, Object>) this.instance.getGson().fromJson(fileReader, (Class) Map.class);
            this.values.putAll(strings);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
