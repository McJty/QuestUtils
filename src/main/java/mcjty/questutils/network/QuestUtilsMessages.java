package mcjty.questutils.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class QuestUtilsMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
//        net.registerMessage(PacketGetGridStatus.Handler.class, PacketGetGridStatus.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
//        net.registerMessage(PacketReturnGridStatus.Handler.class, PacketReturnGridStatus.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }
}
