package mcjty.questutils.items;

import mcjty.lib.McJtyRegister;
import mcjty.questutils.QuestUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ControlKey extends Item {

    public ControlKey() {
        setUnlocalizedName(QuestUtils.MODID + "." + "controlkey");
        setRegistryName("controlkey");
        setCreativeTab(QuestUtils.tabQuestUtils);
        McJtyRegister.registerLater(this, QuestUtils.instance);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.GOLD + "This key is needed to open");
        tooltip.add(TextFormatting.GOLD + "the GUI's of all blocks in this mod");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
