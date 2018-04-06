package mcjty.questutils.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static ControlKey controlKey;

    public static void init() {
        controlKey = new ControlKey();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controlKey.initModel();
    }
}
