package mcjty.questutils;


import mcjty.lib.base.ModBase;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = QuestUtils.MODID, name = QuestUtils.MODNAME,
        dependencies =
                "required-after:mcjtylib_ng@[" + QuestUtils.MIN_MCJTYLIB_VER + ",);" +
                "after:forge@[" + QuestUtils.MIN_FORGE_VER + ",)",
        version = QuestUtils.MODVERSION)
public class QuestUtils implements ModBase {

    public static final String MODID = "questutils";
    public static final String MODNAME = "QuestUtils";
    public static final String MODVERSION = "0.1.0-alpha";
    public static final String MIN_MCJTYLIB_VER = "3.0.0-alpha";

    public static final String MIN_FORGE_VER = "13.19.0.2176";

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
}
