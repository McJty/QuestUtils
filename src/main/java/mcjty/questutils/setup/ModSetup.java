package mcjty.questutils.setup;

import mcjty.lib.setup.DefaultModSetup;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.ModBlocks;
import mcjty.questutils.config.ConfigSetup;
import mcjty.questutils.compat.computers.OpenComputersIntegration;
import mcjty.questutils.items.ModItems;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ModSetup extends DefaultModSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(QuestUtils.instance, new GuiProxy());

        QuestUtilsMessages.registerMessages("questutils");

        ConfigSetup.init();
        ModItems.init();
        ModBlocks.init();
    }

    @Override
    protected void setupModCompat() {
        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }
    }

    @Override
    public void createTabs() {
        createTab("questutils", new ItemStack(Blocks.CRAFTING_TABLE));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
    }
}
