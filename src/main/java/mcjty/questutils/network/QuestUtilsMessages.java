package mcjty.questutils.network;

import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import mcjty.questutils.QuestUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class QuestUtilsMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(QuestUtils.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
//        net.registerMessage(PacketGetGridStatus.Handler.class, PacketGetGridStatus.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
//        net.registerMessage(PacketReturnGridStatus.Handler.class, PacketReturnGridStatus.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }
}
