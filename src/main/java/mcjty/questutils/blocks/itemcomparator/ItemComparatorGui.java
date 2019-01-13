package mcjty.questutils.blocks.itemcomparator;

import mcjty.lib.client.RenderHelper;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.*;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ItemComparatorGui extends GenericGuiContainer<ItemComparatorTE> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 238;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/item_comparator.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    public ItemComparatorGui(ItemComparatorTE tileEntity, ItemComparatorContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "item_comparator");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        TextField idField = new TextField(mc, this)
                .setName("id")
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));

        ToggleButton ignoreNBT = new ToggleButton(mc, this)
                .setName("ignoreNBT")
                .setLayoutHint(new PositionalLayout.PositionalHint(12, 110, 65, 14)).setText("Ignore NBT");
        ToggleButton ignoreMeta = new ToggleButton(mc, this)
                .setName("ignoreMeta")
                .setLayoutHint(new PositionalLayout.PositionalHint(12, 125, 65, 14)).setText("Ignore Meta");

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(idField)
                .addChild(new Label(mc, this).setText("Filter").setLayoutHint(new PositionalLayout.PositionalHint(12, 22, 18*4, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(new Label(mc, this).setText("Buffer").setLayoutHint(new PositionalLayout.PositionalHint(102, 22, 18*4, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(new Button(mc, this).setText("Remember").setLayoutHint(new PositionalLayout.PositionalHint(118, 110, 55, 14)).setChannel("remember"))
                .addChild(new Button(mc, this).setText("Forget").setLayoutHint(new PositionalLayout.PositionalHint(118, 125, 55, 14)).setChannel("forget"))
                .addChild(ignoreNBT)
                .addChild(ignoreMeta)
                ;
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        window.bind(QuestUtilsMessages.INSTANCE, "id", tileEntity, QUTileEntity.VALUE_ID.getName());
        window.bind(QuestUtilsMessages.INSTANCE, "ignoreNBT", tileEntity, ItemComparatorTE.VALUE_IGNORE_NBT.getName());
        window.bind(QuestUtilsMessages.INSTANCE, "ignoreMeta", tileEntity, ItemComparatorTE.VALUE_IGNORE_META.getName());
        window.event("remember", (source, params) -> window.sendAction(QuestUtilsMessages.INSTANCE, tileEntity, ItemComparatorTE.ACTION_REMEMBER));
        window.event("forget", (source, params) -> window.sendAction(QuestUtilsMessages.INSTANCE, tileEntity, ItemComparatorTE.ACTION_FORGET));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
        drawGhostSlots();
    }


    private void drawGhostSlots() {
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0.0F);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (short) 240 / 1.0F, 240.0f);

        InventoryHelper ghostSlots = tileEntity.getGhostSlots();
        zLevel = 100.0F;
        itemRender.zLevel = 100.0F;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        for (int i = 0 ; i < ghostSlots.getCount() ; i++) {
            ItemStack stack = ghostSlots.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int slotIdx = 16 + i;
                Slot slot = inventorySlots.getSlot(slotIdx);
                if (!slot.getHasStack()) {
                    itemRender.renderItemAndEffectIntoGUI(stack, slot.xPos, slot.yPos);

                    GlStateManager.disableLighting();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    this.mc.getTextureManager().bindTexture(iconGuiElements);
                    RenderHelper.drawTexturedModalRect(slot.xPos, slot.yPos, 14 * 16, 3 * 16, 16, 16);
                    GlStateManager.enableDepth();
                    GlStateManager.disableBlend();
                    GlStateManager.enableLighting();
                }
            }

        }
        itemRender.zLevel = 0.0F;
        zLevel = 0.0F;

        GlStateManager.popMatrix();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }
}


