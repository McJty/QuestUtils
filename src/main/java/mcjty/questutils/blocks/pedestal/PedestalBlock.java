package mcjty.questutils.blocks.pedestal;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.questutils.blocks.QUBlock;
import mcjty.questutils.proxy.GuiProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PedestalBlock extends QUBlock<PedestalTE, PedestalContainer> {

    public PedestalBlock() {
        super(PedestalTE.class, PedestalContainer.class, "pedestal");
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_PEDESTAL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return PedestalGui.class;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(PedestalTE.class, new PedestalTESR());
    }

    @Override
    public boolean handleModule(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PedestalTE) {
            PedestalMode mode = ((PedestalTE) te).getMode();
            switch (mode) {
                case MODE_DISPLAY:
                    return false;
                case MODE_INTERACT:
                    break;
                case MODE_PLACE:
                    break;
                case MODE_TAKE:
                    break;
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    private PedestalTE getTE(World world, BlockPos pos) {
        return (PedestalTE) world.getTileEntity(pos);
    }
}
