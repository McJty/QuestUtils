package mcjty.questutils.blocks.screen;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScreenModel extends ModelBase {

    private ModelRenderer renderer = new ModelRenderer(this, 0, 0);

    public ScreenModel(int size) {
        this.renderer.addBox(-8.0F, -8.0F, -1.0F, 16 * (size+1), 16 * (size+1), 2, 0.0F);
        this.renderer.setTextureSize(16, 16);
    }

    public void render() {
        this.renderer.render(0.0625F);
    }

}
