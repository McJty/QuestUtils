package mcjty.questutils.apiimp;

import mcjty.lib.varia.WorldTools;
import mcjty.questutils.api.IQuestUtils;
import mcjty.questutils.api.IScreen;
import mcjty.questutils.data.QUData;
import mcjty.questutils.data.QUEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class QuestUtilsApi implements IQuestUtils {

    @Override
    public IScreen findScreen(String id) {
        QUEntry entry = QUData.getData().getEntry(id);
        if (entry != null) {
            WorldServer world = DimensionManager.getWorld(entry.getDimension());
            if (world == null) {
                world = DimensionManager.getWorld(0).getMinecraftServer().getWorld(entry.getDimension());
                if (world == null) {
                    return null;
                }
            }
            TileEntity te = world.getTileEntity(entry.getPos());
            if (te instanceof IScreen) {
                return (IScreen) te;
            }
        }
        return null;
    }

}

