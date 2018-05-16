package mcjty.questutils.blocks.pedestal;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class PedestalGui extends GenericGuiContainer<PedestalTE> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 238;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/pedestal.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    public PedestalGui(PedestalTE tileEntity, PedestalContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "pedestal");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        TextField idField = new TextField(mc, this)
                .setName("id")
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));

        ChoiceLabel modeChoice = new ChoiceLabel(mc, this)
                .setName("mode")
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 37, 60, 16));
        for (PedestalMode mode : PedestalMode.values()) {
            modeChoice.addChoices(mode.getName());
            modeChoice.setChoiceTooltip(mode.getName(), mode.getTooltip());
        }

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(idField).addChild(modeChoice);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        window.bind(QuestUtilsMessages.INSTANCE, "mode", tileEntity, PedestalTE.VALUE_MODE.getName());
        window.bind(QuestUtilsMessages.INSTANCE, "id", tileEntity, QUTileEntity.VALUE_ID.getName());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
