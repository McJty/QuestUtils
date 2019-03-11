package mcjty.questutils.setup;

import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import mcjty.questutils.QuestUtils;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(QuestUtils.MODID);
//        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        McJtyLibClient.preInit(e);
    }
}
