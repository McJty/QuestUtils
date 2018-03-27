package mcjty.questutils.blocks.screen;

import mcjty.lib.container.BaseBlock;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.ModBlocks;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ScreenRenderer extends TileEntitySpecialRenderer<ScreenTE> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(QuestUtils.MODID, "textures/blocks/screenframe.png");
    private final ScreenModel screenModel = new ScreenModel(ScreenTE.SIZE_NORMAL);
    private final ScreenModel screenModelLarge = new ScreenModel(ScreenTE.SIZE_LARGE);
    private final ScreenModel screenModelHuge = new ScreenModel(ScreenTE.SIZE_HUGE);
    private final ScreenModel screenModelEnourmous = new ScreenModel(ScreenTE.SIZE_ENOURMOUS);
    private final ScreenModel screenModelGigantic = new ScreenModel(ScreenTE.SIZE_GIGANTIC);

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
            renderScreenBoard(tileEntity.getSize(), tileEntity.getColor());
        }

        if (tileEntity != null) {
            FontRenderer fontrenderer = this.getFontRenderer();

//            IClientScreenModule.TransformMode mode = IClientScreenModule.TransformMode.NONE;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

//            Map<Integer, IModuleData> screenData = updateScreenData(tileEntity);

//            List<IClientScreenModule> modules = tileEntity.getClientScreenModules();
//            if (tileEntity.isShowHelp()) {
//                modules = ScreenTileEntity.getHelpingScreenModules();
//            }
            renderModules(fontrenderer, tileEntity, tileEntity.getSize());
        }

        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

//    private Map<Integer, IModuleData> updateScreenData(ScreenTileEntity screenTileEntity) {
//        long millis = System.currentTimeMillis();
//        if ((millis - screenTileEntity.lastTime > 500) && screenTileEntity.isNeedsServerData()) {
//            screenTileEntity.lastTime = millis;
//            GlobalCoordinate pos = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getWorld().provider.getDimension());
//            RFToolsMessages.INSTANCE.sendToServer(new PacketGetScreenData(RFTools.MODID, pos, millis));
//        }
//
//        GlobalCoordinate key = new GlobalCoordinate(screenTileEntity.getPos(), screenTileEntity.getWorld().provider.getDimension());
//        Map<Integer,IModuleData> screenData = ScreenTileEntity.screenData.get(key);
//        if (screenData == null) {
//            screenData = Collections.emptyMap();
//        }
//        return screenData;
//    }

//    private ClientScreenModuleHelper clientScreenModuleHelper = new ClientScreenModuleHelper();

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

        if (tileEntity.isBright()) {
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }

        if (tileEntity.getTitle() != null) {
            int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(tileEntity.getTitle()) * 2;
            int textx = 2;
            textx += (128-width) / 4;
            RenderTools.renderText(textx, 8, tileEntity.getTitle(), factor*2, 0xffffff00);
        }
        RenderTools.renderItem(15, 18, tileEntity.getStackInSlot(ScreenContainer.SLOT_ITEM), factor);
        String[] status = tileEntity.getStatus();
        int currenty = 95;
        for (String s : status) {
            if (s != null) {
                RenderTools.renderText(10, currenty, s, factor, 0xffffffff);
                currenty += 10;
            }
        }

        if (tileEntity.isBright()) {
            Minecraft.getMinecraft().entityRenderer.enableLightmap();
        }
    }

    private void renderScreenBoard(int size, int color) {
        this.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1, -1, -1);
        if (size == ScreenTE.SIZE_GIGANTIC) {
            this.screenModelGigantic.render();
        } else if (size == ScreenTE.SIZE_ENOURMOUS) {
            this.screenModelEnourmous.render();
        } else if (size == ScreenTE.SIZE_HUGE) {
            this.screenModelHuge.render();
        } else if (size == ScreenTE.SIZE_LARGE) {
            this.screenModelLarge.render();
        } else {
            this.screenModel.render();
        }

        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        float dim = size + .46f;
        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;
        renderer.pos(-.46f, dim, -0.08f).color(r, g, b, 1f).endVertex();
        renderer.pos(dim, dim, -0.08f).color(r, g, b, 1f).endVertex();
        renderer.pos(dim, -.46f, -0.08f).color(r, g, b, 1f).endVertex();
        renderer.pos(-.46f, -.46f, -0.08f).color(r, g, b, 1f).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }
}
