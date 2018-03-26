package mcjty.questutils.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderTools {

    public static void renderItem(int x, int y, ItemStack stack, float factor) {

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.depthMask(true);

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

        GlStateManager.pushMatrix();
        float f3 = 0.0075F * 3;
        GlStateManager.translate(-0.5F, 0.5F, 0.06F);
        GlStateManager.scale(f3 * factor, -f3 * factor, 0.0001f);

//        short short1 = 240;
//        short short2 = 240;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
        renderSlot(y-5, stack, 0, x);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, 0.5F, 0.08F);
        GlStateManager.scale(f3 * factor, -f3 * factor, 0.0001f);

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        renderSlotOverlay(fontRenderer, y, stack, 0, x);
        GlStateManager.popMatrix();

        GlStateManager.disableLighting();
        RenderHelper.enableStandardItemLighting();
    }

    private static int renderSlot(int currenty, ItemStack itm, int index, int x) {
        if (!itm.isEmpty()) {
            RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
            itemRender.renderItemAndEffectIntoGUI(itm, x, currenty);
        }
        x += 30;
        return x;
    }

    private static int renderSlotOverlay(FontRenderer fontRenderer, int currenty, ItemStack itm, int index, int x) {
        renderBorder(x, currenty);
        if (!itm.isEmpty()) {
//                itemRender.renderItemOverlayIntoGUI(fontRenderer, Minecraft.getMinecraft().getTextureManager(), itm, x, currenty);
            renderItemOverlayIntoGUI(fontRenderer, itm, x, currenty);
        }
        x += 30;
        return x;
    }

    private static void renderBorder(int x, int y) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        renderQuad(tessellator, x-1, y-6, 18, 1, 255, 128, 0, 0);
        renderQuad(tessellator, x-1, y+11, 18, 1, 255, 128, 0, 0);
        renderQuad(tessellator, x-1, y-5, 1, 16, 255, 128, 0, 0);
        renderQuad(tessellator, x+16, y-5, 1, 16, 255, 128, 0, 0);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
//                GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    }

    private static void renderItemOverlayIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y) {
        if (!itemStack.isEmpty()) {
            int size = itemStack.getCount();
            if (size > 1) {
                String s1;
                if (size < 10000) {
                    s1 = String.valueOf(size);
                } else if (size < 1000000) {
                    s1 = String.valueOf(size / 1000) + "k";
                } else if (size < 1000000000) {
                    s1 = String.valueOf(size / 1000000) + "m";
                } else {
                    s1 = String.valueOf(size / 1000000000) + "g";
                }
                GlStateManager.disableLighting();
//                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GlStateManager.disableBlend();
                fontRenderer.drawString(s1, x + 19 - 2 - fontRenderer.getStringWidth(s1), y + 6 + 3, 16777215);
                GlStateManager.enableLighting();
//                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (itemStack.getItem().showDurabilityBar(itemStack)) {
                double health = itemStack.getItem().getDurabilityForDisplay(itemStack);
                int j1 = (int) Math.round(13.0D - health * 13.0D);
                int k = (int) Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
//                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                int rgbfordisplay = itemStack.getItem().getRGBDurabilityForDisplay(itemStack);

                Tessellator tessellator = Tessellator.getInstance();
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = rgbfordisplay;
                renderQuad(tessellator, x + 2 + i, y + 13, 13-i, 2, 0, 0, 0, 255);
                renderQuad(tessellator, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
//                GlStateManager.enableDepth();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public static void renderQuad(Tessellator tessellator, int x, int y, int width, int height, int r, int g, int b, int a) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
//        tessellator.setColorOpaque_I(color);
        buffer.pos(x, y, 100).color(r, g, b, a).endVertex();
        buffer.pos(x, (y + height), 100).color(r, g, b, a).endVertex();
        buffer.pos((x + width), (y + height), 100).color(r, g, b, a).endVertex();
        buffer.pos((x + width), y, 100).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    public static void renderText(int x, int y, String text, float factor, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, 0.5F, 0.07F);
        float f3 = 0.0075F;
        GlStateManager.scale(f3 * factor, -f3 * factor, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableLighting();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString(text, x, y, color);

        GlStateManager.popMatrix();
    }
}
