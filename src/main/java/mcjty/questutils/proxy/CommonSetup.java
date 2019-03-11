package mcjty.questutils.proxy;

import mcjty.lib.McJtyRegister;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.ModBlocks;
import mcjty.questutils.config.ConfigSetup;
import mcjty.questutils.integration.computers.OpenComputersIntegration;
import mcjty.questutils.items.ModItems;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);
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

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        McJtyRegister.registerBlocks(QuestUtils.instance, event.getRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(QuestUtils.instance, event.getRegistry());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
    }
}
