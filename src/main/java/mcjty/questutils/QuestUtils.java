package mcjty.questutils;


import mcjty.lib.base.ModBase;
import mcjty.questutils.api.IQuestUtils;
import mcjty.questutils.apiimp.QuestUtilsApi;
import mcjty.questutils.commands.CmdQU;
import mcjty.questutils.integration.computers.OpenComputersIntegration;
import mcjty.questutils.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Function;

@Mod(modid = QuestUtils.MODID, name = QuestUtils.MODNAME,
        dependencies =
                "required-after:mcjtylib_ng@[" + QuestUtils.MIN_MCJTYLIB_VER + ",);" +
                "after:forge@[" + QuestUtils.MIN_FORGE_VER + ",)",
        version = QuestUtils.MODVERSION)
public class QuestUtils implements ModBase {

    public static final String MODID = "questutils";
    public static final String MODNAME = "QuestUtils";
    public static final String MODVERSION = "0.3.0";
    public static final String MIN_MCJTYLIB_VER = "3.0.0";

    public static final String MIN_FORGE_VER = "13.19.0.2176";

    public static QuestUtilsApi questUtilsApi = new QuestUtilsApi();

    @SidedProxy(clientSide = "mcjty.questutils.proxy.ClientProxy", serverSide = "mcjty.questutils.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static QuestUtils instance;

    public static Logger logger;

    public static CreativeTabs tabQuestUtils = new CreativeTabs("questutils") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
        if (Loader.isModLoaded("opencomputers")) {
            OpenComputersIntegration.init();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CmdQU());
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookindex, String page) {

    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("getQuestUtils")) {
                Optional<Function<IQuestUtils, Void>> value = message.getFunctionValue(IQuestUtils.class, Void.class);
                if (value.isPresent()) {
                    value.get().apply(questUtilsApi);
                } else {
                    logger.warn("Some mod didn't return a valid result with getQuestUtils!");
                }
            }
        }
    }

}
