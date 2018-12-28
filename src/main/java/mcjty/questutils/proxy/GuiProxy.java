package mcjty.questutils.proxy;

import mcjty.lib.blocks.GenericBlock;
import mcjty.questutils.blocks.rewardchest.RewardChestGui;
import mcjty.questutils.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    public static final int GUI_ITEM_COMPARATOR = 0;
    public static final int GUI_SCREEN = 1;
    public static final int GUI_PEDESTAL = 2;
    public static final int GUI_REWARD_CHEST = 3;

    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createServerContainer(entityPlayer, te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            GuiContainer clientGui = genericBlock.createClientGui(entityPlayer, te);
            if (guiid == GUI_REWARD_CHEST) {
                if (entityPlayer.getHeldItemMainhand().getItem() == ModItems.controlKey) {
                    ((RewardChestGui) clientGui).enableId();
                }
            }
            return clientGui;
        }
        return null;
    }
}
