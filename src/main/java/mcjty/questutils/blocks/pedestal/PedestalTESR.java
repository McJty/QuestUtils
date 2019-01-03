package mcjty.questutils.blocks.pedestal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PedestalTESR extends TileEntitySpecialRenderer<PedestalTE> {
    @Override
    public void render(PedestalTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Render our item
        renderItem(te);

        GlStateManager.popMatrix();
    }

    private void renderItem(PedestalTE te) {
        ItemStack stack = te.getStackInSlot(PedestalContainer.SLOT_ITEM);
        if (!stack.isEmpty()) {
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            // Translate to the center of the block and .9 points higher
            GlStateManager.translate(.5, .9, .5);
            GlStateManager.scale(.4f, .4f, .4f);
            long angle = (System.currentTimeMillis() / 50) % 360;
            GlStateManager.rotate(angle, 0, 1, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(PedestalTE.class, new PedestalTESR());
    }
}
