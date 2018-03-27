package mcjty.questutils.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static boolean BRIGHT_SCREENS = false;

    public static void init(Configuration cfg) {
        BRIGHT_SCREENS = cfg.getBoolean("brightScreens", CATEGORY_GENERAL, BRIGHT_SCREENS, "If this is true all screens will be full bright (even in the dark)");
    }
}
