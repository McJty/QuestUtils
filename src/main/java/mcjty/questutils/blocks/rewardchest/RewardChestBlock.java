package mcjty.questutils.blocks.rewardchest;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.questutils.blocks.QUBlock;
import mcjty.questutils.setup.GuiProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

public class RewardChestBlock extends QUBlock<RewardChestTE, RewardChestContainer> {

    public RewardChestBlock() {
        super(RewardChestTE.class, RewardChestContainer::new, "reward_chest");
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_REWARD_CHEST;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiFunction<RewardChestTE, RewardChestContainer, GenericGuiContainer<? super RewardChestTE>> getGuiFactory() {
        return RewardChestGui::new;
    }

    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        return false;
    }
}
