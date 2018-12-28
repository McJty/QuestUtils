package mcjty.questutils.blocks.rewardchest;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class RewardChestGui extends GenericGuiContainer<RewardChestTE> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 208;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/reward_chest.png");

    private boolean idEnabled = false;

    public RewardChestGui(RewardChestTE tileEntity, RewardChestContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "reward_chest");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout());
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        if (idEnabled) {
            TextField idField = new TextField(mc, this)
                    .setName("id")
                    .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));

            toplevel
                    .addChild(new Label(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                    .addChild(idField);
        }

        window = new Window(this, toplevel);

        if (idEnabled) {
            window.bind(QuestUtilsMessages.INSTANCE, "id", tileEntity, QUTileEntity.VALUE_ID.getName());
        }
    }

    public void enableId() {
        idEnabled = true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }

}


