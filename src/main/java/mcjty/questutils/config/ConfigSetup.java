package mcjty.questutils.config;

import mcjty.questutils.QuestUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigSetup {

    public static final String CATEGORY_GENERAL = "general";

    public static boolean BRIGHT_SCREENS = false;

    private static Configuration mainConfig;

    public static void init() {
        mainConfig = new Configuration(new File(QuestUtils.setup.getModConfigDir().getPath(), "questutils.cfg"));
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(ConfigSetup.CATEGORY_GENERAL, "General settings");
            BRIGHT_SCREENS = cfg.getBoolean("brightScreens", CATEGORY_GENERAL, BRIGHT_SCREENS, "If this is true all screens will be full bright (even in the dark)");
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        }
    }

    public static void postInit() {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
    }
}
