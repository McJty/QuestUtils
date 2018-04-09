package mcjty.questutils.blocks.pedestal;

import java.util.HashMap;
import java.util.Map;

public enum PedestalMode {
    MODE_DISPLAY("Display", "Display mode, no interaction"),
    MODE_INTERACT("Interact", "Players can put in items and take them again"),
    MODE_PLACE("Place", "Players can only hand in items"),
    MODE_TAKE("Take", "Players can only take items");

    private final String name;
    private final String tooltip;

    private static final Map<String,PedestalMode> MODE_MAP = new HashMap<>();

    static {
        for (PedestalMode mode : PedestalMode.values()) {
            MODE_MAP.put(mode.getName().toLowerCase(), mode);
        }
    }

    PedestalMode(String name, String tooltip) {
        this.name = name;
        this.tooltip = tooltip;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public static PedestalMode getModeByName(String name) {
        return MODE_MAP.get(name.toLowerCase());
    }
}
