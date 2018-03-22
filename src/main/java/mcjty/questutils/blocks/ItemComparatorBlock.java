package mcjty.questutils.blocks;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.proxy.GuiProxy;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemComparatorBlock extends GenericBlock<ItemComparatorTE, ItemComparatorContainer> {

    public ItemComparatorBlock() {
        super(QuestUtils.instance, Material.IRON, ItemComparatorTE.class, ItemComparatorContainer.class, "item_comparator", true);
    }

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    @Override
    public boolean hasRedstoneOutput() {
        return true;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_ITEM_COMPARATOR;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return ItemComparatorGui.class;
    }

    @Override
    protected int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        EnumFacing direction = state.getValue(FACING);
        if (side == direction) {
            TileEntity te = world.getTileEntity(pos);
            if (state.getBlock() instanceof ItemComparatorBlock && te instanceof ItemComparatorTE) {
                ItemComparatorTE comparator = (ItemComparatorTE) te;
                return comparator.isPowered() ? 15 : 0;
            }
        }
        return 0;
    }

}
