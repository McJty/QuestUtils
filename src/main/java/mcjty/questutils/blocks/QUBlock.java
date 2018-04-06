package mcjty.questutils.blocks;

import mcjty.lib.container.GenericBlock;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.items.ModItems;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public abstract class QUBlock <T extends QUTileEntity, C extends Container> extends GenericBlock<T, C> {

    public QUBlock(Class<? extends T> tileEntityClass, Class<? extends C> containerClass, String name) {
        super(QuestUtils.instance, Material.IRON, tileEntityClass, containerClass, name, true);
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        if (mode == ProbeMode.EXTENDED) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof QUTileEntity) {
                probeInfo.text(TextStyleClass.LABEL + "Id: " + TextStyleClass.INFO + ((QUTileEntity) te).getIdentifier());
            }
        }
    }

    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        if (player.getHeldItem(player.getActiveHand()).getItem() == ModItems.controlKey) {
            return super.checkAccess(world, player, te);
        }
        return true;
    }
}
