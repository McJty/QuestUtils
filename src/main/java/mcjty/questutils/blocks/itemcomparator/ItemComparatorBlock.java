package mcjty.questutils.blocks.itemcomparator;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.questutils.blocks.QUBlock;
import mcjty.questutils.proxy.GuiProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

public class ItemComparatorBlock extends QUBlock<ItemComparatorTE, ItemComparatorContainer> {

    public ItemComparatorBlock() {
        super(ItemComparatorTE.class, ItemComparatorContainer::new, "item_comparator");
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
    public BiFunction<ItemComparatorTE, ItemComparatorContainer, GenericGuiContainer<? super ItemComparatorTE>> getGuiFactory() {
        return ItemComparatorGui::new;
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
