package mcjty.questutils.blocks.screen;

import mcjty.lib.blocks.BaseBlock;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.api.FormattedString;
import mcjty.questutils.blocks.ModBlocks;
import mcjty.questutils.config.ConfigSetup;
import mcjty.questutils.rendering.ImageLoader;
import mcjty.questutils.rendering.RenderTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ScreenRenderer extends TileEntitySpecialRenderer<ScreenTE> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(QuestUtils.MODID, "textures/blocks/screenframe.png");

    @Override
    public void render(ScreenTE tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float xRotation = 0.0F;
        float yRotation = 0.0F;

        EnumFacing facing = EnumFacing.SOUTH;
        EnumFacing horizontalFacing = EnumFacing.SOUTH;
        if (tileEntity != null) {
            IBlockState state = Minecraft.getMinecraft().world.getBlockState(tileEntity.getPos());
            if (state.getBlock() instanceof ScreenBlock) {
                facing = state.getValue(BaseBlock.FACING);
                horizontalFacing = state.getValue(ScreenBlock.HORIZONTAL_FACING);
            } else {
                return;
            }
        }

        GlStateManager.pushMatrix();

        switch (horizontalFacing) {
            case NORTH:
                yRotation = -180.0F;
                break;
            case WEST:
                yRotation = -90.0F;
                break;
            case EAST:
                yRotation = 90.0F;
        }
        switch (facing) {
            case DOWN:
                xRotation = 90.0F;
                break;
            case UP:
                xRotation = -90.0F;
        }

        // TileEntity can be null if this is used for an item renderer.
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GlStateManager.rotate(yRotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(xRotation, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.4375F);

        if (tileEntity == null) {
            GlStateManager.disableLighting();
            renderScreenBoard(0, 0);
        } else if (!tileEntity.isTransparent()) {
            GlStateManager.disableLighting();
            renderScreenBoard(tileEntity.getSize().ordinal(), tileEntity.getBackgroundColor());
        }

        if (tileEntity != null) {
            FontRenderer fontrenderer = this.getFontRenderer();

            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            renderModules(fontrenderer, tileEntity, tileEntity.getSize().ordinal());
        }

        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

    private void renderModules(FontRenderer fontrenderer, ScreenTE tileEntity, int size) {
        float f3;
        float factor = size + 1.0f;
        int moduleIndex = 0;

        BlockPos pos = tileEntity.getPos();

        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        IBlockState blockState = getWorld().getBlockState(pos);
        Block block = blockState.getBlock();
        if (block != ModBlocks.screenBlock && block != ModBlocks.screenHitBlock) {
            // Safety
            return;
        }
        if (mouseOver != null) {
            if (mouseOver.sideHit == blockState.getValue(BaseBlock.FACING)) {
                double xx = mouseOver.hitVec.x - pos.getX();
                double yy = mouseOver.hitVec.y - pos.getY();
                double zz = mouseOver.hitVec.z - pos.getZ();
                EnumFacing horizontalFacing = blockState.getValue(ScreenBlock.HORIZONTAL_FACING);
//                hit = tileEntity.getHitModule(xx, yy, zz, mouseOver.sideHit, horizontalFacing);
//                if (hit != null) {
//                    hitModule = modules.get(hit.getModuleIndex());
//                }
//                tileEntity.focusModuleClient(xx, yy, zz, mouseOver.sideHit, horizontalFacing);
            }
        }

        if (ConfigSetup.BRIGHT_SCREENS) {
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }

        renderTitle(tileEntity, factor);
        renderObjective(tileEntity, factor);
        renderStatus(tileEntity, factor);

        if (ConfigSetup.BRIGHT_SCREENS) {
            Minecraft.getMinecraft().entityRenderer.enableLightmap();
        }
    }

    private void renderTitle(ScreenTE tileEntity, float factor) {
        if (tileEntity.getTitle() != null) {
            String txt = tileEntity.getTitle().getText();
            int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(txt);
            int textx = 4;
            switch (tileEntity.getTitle().getAlignment()) {
                case LEFT:
                    break;
                case CENTER:
                    textx += (60-width) / 2;
                    break;
                case RIGHT:
                    textx += (60-width);
                    break;
            }
            RenderTools.renderText(textx, 8, txt, factor*2, tileEntity.getTitle().getColor());
        }
    }

    private static final ItemStack[] STACKS = new ItemStack[9];

    private void renderObjective(ScreenTE tileEntity, float factor) {
        ResourceLocation icon = tileEntity.getIcon();
        if (icon != null) {
            String filename = tileEntity.getFilename();
            if (filename != null && !filename.trim().isEmpty()) {
                File file;
                if (filename.startsWith("$")) {
                    file = new File(QuestUtils.setup.getModConfigDir().getPath() + File.separator + filename.substring(1));
                } else {
                    file = new File(filename);
                }

                ImageLoader.loadAndBind(icon, file);
            }
            RenderTools.renderIcon(15, 18, icon, factor, tileEntity.getBorderColor());
        } else {
            int cnt = 0;
            for (int i = 0 ; i < 9 ; i++) {
                if (!tileEntity.getStackInSlot(ScreenContainer.SLOT_ITEM+i).isEmpty()) {
                    STACKS[cnt++] = tileEntity.getStackInSlot(i);
                }
            }
            for (int i = cnt ; i < 9 ; i++) {
                STACKS[i] = ItemStack.EMPTY;
            }

            if (cnt <= 1) {
                RenderTools.renderItem(15, 18, STACKS[0], factor, tileEntity.getBorderColor());
            } else if (cnt <= 4) {
                float v = 0.5f;
                RenderTools.renderItem(25, 29, STACKS[0], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(45, 29, STACKS[1], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(25, 49, STACKS[2], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(45, 49, STACKS[3], factor * v, tileEntity.getBorderColor());
            } else {
                float v = 0.3f;
                RenderTools.renderItem(43, 47, STACKS[0], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(63, 47, STACKS[1], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(83, 47, STACKS[2], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(43, 67, STACKS[3], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(63, 67, STACKS[4], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(83, 67, STACKS[5], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(43, 87, STACKS[6], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(63, 87, STACKS[7], factor * v, tileEntity.getBorderColor());
                RenderTools.renderItem(83, 87, STACKS[8], factor * v, tileEntity.getBorderColor());
            }
        }
    }

    private void renderStatus(ScreenTE tileEntity, float factor) {
        FormattedString[] status = tileEntity.getStatus();
        int currenty = 95;
        for (FormattedString s : status) {
            if (s != null) {
                int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(s.getText());
                int textx = 10;
                switch (s.getAlignment()) {
                    case LEFT:
                        break;
                    case CENTER:
                        textx += (116-width) / 2;
                        break;
                    case RIGHT:
                        textx += (116-width);
                        break;
                }
                RenderTools.renderText(textx, currenty, s.getText(), factor, s.getColor());
                currenty += 10;
            }
        }
    }

    private void renderScreenBoard(int size, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();

        this.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1, -1, -1);


        float dim = size + .46f;
        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(-.46f, dim, 0).tex(0, 0).endVertex();
        renderer.pos(-.46f, -.46f, 0).tex(0, 1).endVertex();
        renderer.pos(dim, -.46f, 0).tex(1, 1).endVertex();
        renderer.pos(dim, dim, 0).tex(1, 0).endVertex();

        renderer.pos(-.46f, dim, 0).tex(0, 0).endVertex();
        renderer.pos(dim, dim, 0).tex(0, 0).endVertex();
        renderer.pos(dim, dim, -.1).tex(0, 0).endVertex();
        renderer.pos(-.46f, dim, -.1).tex(0, 0).endVertex();

        renderer.pos(-.46f, -.46f, 0).tex(0, 0).endVertex();
        renderer.pos(-.46f, -.46f, -.1).tex(0, 0).endVertex();
        renderer.pos(dim, -.46f, -.1).tex(0, 0).endVertex();
        renderer.pos(dim, -.46f, 0).tex(0, 0).endVertex();

        renderer.pos(-.46f, -.46f, 0).tex(0, 0).endVertex();
        renderer.pos(-.46f, dim, 0).tex(0, 0).endVertex();
        renderer.pos(-.46f, dim, -.1).tex(0, 0).endVertex();
        renderer.pos(-.46f, -.46f, -.1).tex(0, 0).endVertex();

        renderer.pos(dim, -.46f, 0).tex(0, 0).endVertex();
        renderer.pos(dim, -.46f, -.1).tex(0, 0).endVertex();
        renderer.pos(dim, dim, -.1).tex(0, 0).endVertex();
        renderer.pos(dim, dim, 0).tex(0, 0).endVertex();

        tessellator.draw();

        GlStateManager.disableTexture2D();

        GlStateManager.depthMask(false);
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        float z = -0.08f;
        renderer.pos(-.46f, dim, z).color(r, g, b, 1f).endVertex();
        renderer.pos(dim, dim, z).color(r, g, b, 1f).endVertex();
        renderer.pos(dim, -.46f, z).color(r, g, b, 1f).endVertex();
        renderer.pos(-.46f, -.46f, z).color(r, g, b, 1f).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();


        GlStateManager.popMatrix();
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(ScreenTE.class, new ScreenRenderer());
    }
}
