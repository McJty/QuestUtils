package mcjty.questutils.blocks.pedestal;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Argument;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class PedestalGui extends GenericGuiContainer<PedestalTE> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 238;

    private TextField idField;
    private ChoiceLabel modeChoice;

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

        idField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));
        idField.setText(tileEntity.getIdentifier() == null ? "" : tileEntity.getIdentifier());
        idField.addTextEvent((parent, newText) -> {
            updateId();
        });

        modeChoice = new ChoiceLabel(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 37, 60, 16));
        for (PedestalMode mode : PedestalMode.values()) {
            modeChoice.addChoices(mode.getName());
            modeChoice.setChoiceTooltip(mode.getName(), mode.getTooltip());
        }
        modeChoice.setChoice(tileEntity.getMode().getName());
        modeChoice.addChoiceEvent((parent, newChoice) -> updateMode());

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label<>(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(idField).addChild(modeChoice);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void updateMode() {
        tileEntity.setMode(PedestalMode.getModeByName(modeChoice.getCurrentChoice()));
        sendServerCommand(QuestUtilsMessages.INSTANCE, PedestalTE.CMD_SETMODE, new Argument("mode", modeChoice.getCurrentChoice()));
    }

    private void updateId() {
        tileEntity.setIdentifier(idField.getText());
        sendServerCommand(QuestUtilsMessages.INSTANCE, QUTileEntity.CMD_SETID, new Argument("id", idField.getText()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
