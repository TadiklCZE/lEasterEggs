package eastereggs;

import eastereggs.Managers.EasterEggsCommand;
import eastereggs.Managers.Egg;
import eastereggs.Managers.Runnables.ChangerandomSkinRunnable;
import eastereggs.Managers.Runnables.CheckEggsRunnable;
import eastereggs.Managers.Runnables.ParticlesRunnable;
import eastereggs.Managers.Runnables.SaveConfigRunnable;
import eastereggs.Managers.StorageManager;
import eastereggs.PlaceholderAPI.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Eastereggs extends JavaPlugin {

    private File dataFile;
    private YamlConfiguration modifyDataFile;
    private File configFile;
    private YamlConfiguration modifyConfigFile;
    StorageManager storage = new StorageManager(this);

    @Override
    public void onEnable() {


        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&d[lEasterEggs]&f Plugin was &aEnabled&f!"));

        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        saveDefaultConfig();

        try {
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage.loadEggs();
        storage.loadEggPlayers();

        getCommand("eastereggs").setExecutor(new EasterEggsCommand(this,storage));
        getServer().getPluginManager().registerEvents(new EggsListener(this),this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this,storage).register();
        }
        new ChangerandomSkinRunnable(this,storage).runTaskTimerAsynchronously(this,40,30);
        new CheckEggsRunnable(this,storage).runTask(this);
        new ParticlesRunnable(this,storage).runTaskTimerAsynchronously(this,40,10);
        new SaveConfigRunnable(this,storage).runTaskTimer(this,40,100);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&d[lEasterEggs]&f Plugin was &cDisabled&f!"));
        try {
            storage.saveEggs();
            storage.saveEggPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public YamlConfiguration getDataFile() {
        return modifyDataFile;
    }
    public YamlConfiguration getConfigFile() {
        return modifyConfigFile;
    }

    public File getFile() {
        return dataFile;
    }


    public void loadFiles() throws IOException {
        dataFile = new File(getDataFolder(), "data.yml");
        configFile = new File(getDataFolder(),"config.yml");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }


        modifyDataFile = YamlConfiguration.loadConfiguration(dataFile);
        modifyConfigFile = YamlConfiguration.loadConfiguration(configFile);
    }

    public int getConfigInt(String path, int defaultValue) {
        if (getConfigFile().getString(path)!=null)
            return getConfigFile().getInt(path);
        else {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c[EasterEggs] Path '"+path+"' is missing, please add it! (Using default value)"));
            return defaultValue;
        }
    }
    public List<String> getConfigList(String path, List<String> defaultValue) {
        if (getConfigFile().getStringList(path)!=null && !getConfigFile().getStringList(path).isEmpty()) {
            return getConfigFile().getStringList(path);
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c[EasterEggs] Path '"+path+"' is missing, please add it! (Using default value)"));
            return defaultValue;
        }
    }

    public boolean getConfigBoolean(String path, Boolean defaultValue) {
        if (getConfigFile().getString(path)!=null)
            return getConfigFile().getBoolean(path);
        else {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c[EasterEggs] Path '"+path+"' is missing, please add it! (Using default value)"));
            return defaultValue;
        }
    }
    public Color getConfigColor(String path, Color defaultValue) {
        if (getConfigFile().getString(path)!=null)
            switch (getConfig().getString(path)) {
                case "AQUA":
                    return Color.AQUA;
                case "FUCHSIA":
                    return Color.FUCHSIA;
                case "PURPLE":
                    return Color.PURPLE;
                case "BLUE":
                    return Color.BLUE;
                case "GREEN":
                    return Color.GREEN;
                case "LIME":
                    return Color.LIME;
                case "MAROON":
                    return Color.MAROON;
                case "NAVY":
                    return Color.NAVY;
                case "ORANGE":
                    return Color.ORANGE;
                case "YELLOW":
                    return Color.YELLOW;
                case "BLACK":
                    return Color.BLACK;
                case "SILVER":
                    return Color.SILVER;
                case "GRAY":
                    return Color.GRAY;
                default:
                    return Color.WHITE;
            }
        else {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c[EasterEggs] Path '"+path+"' is missing, please add it! (Using default value)"));
            return defaultValue;
        }
    }

    public String getConfigString(String path, String message) {
        if (getConfigFile().getString(path)!=null)
            return getConfigFile().getString(path);
        else {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c[EasterEggs] Path '"+path+"' is missing, please add it! (Using default value)"));
            return message;
        }
    }

    public void saveEgg(Location loc, List<String> commands, Player p) {
        Egg egg = new Egg(loc,commands);
        storage.addEgg(egg);

    }

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',message.replace("%found%",String.valueOf(storage.foundEggVar(p.getUniqueId()))).replace("%total%",String.valueOf(storage.totalEggVar())).replace("%remain%",String.valueOf(storage.remainEggVar(p.getUniqueId())))));
    }

}
