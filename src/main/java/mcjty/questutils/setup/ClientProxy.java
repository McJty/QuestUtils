package mcjty.questutils.setup;

import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.ModBlocks;
import mcjty.questutils.items.ModItems;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        super.preInit(e);
        OBJLoader.INSTANCE.addDomain(QuestUtils.MODID);
//        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        McJtyLibClient.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
        ModBlocks.initModels();
    }


    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
//        SoundController.init(sounds.getRegistry());
    }


    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
//        ModBlocks.initItemModels();
    }
}
