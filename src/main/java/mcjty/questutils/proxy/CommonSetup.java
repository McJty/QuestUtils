package mcjty.questutils.proxy;

import mcjty.lib.McJtyRegister;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.ModBlocks;
import mcjty.questutils.config.GeneralConfiguration;
import mcjty.questutils.integration.computers.OpenComputersIntegration;
import mcjty.questutils.items.ModItems;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);

        mainConfig = new Configuration(new File(modConfigDir.getPath(), "questutils.cfg"));

        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(QuestUtils.MODID, "questutils");
        QuestUtilsMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
    }

    @Override
    public void createTabs() {
        createTab("questutils", new ItemStack(Blocks.CRAFTING_TABLE));
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        McJtyRegister.registerBlocks(QuestUtils.instance, event.getRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(QuestUtils.instance, event.getRegistry());
    }



    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(QuestUtils.instance, new GuiProxy());
        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }
//        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
//        ModRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        mainConfig = null;
    }
}
