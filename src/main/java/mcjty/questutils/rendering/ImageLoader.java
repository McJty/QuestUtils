package mcjty.questutils.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static void loadAndBind(ResourceLocation id, String filename) {
        ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(id);
        if (texture != null) {
            // Already loaded
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new File(filename));
            texture = new DynamicTexture(image);
            Minecraft.getMinecraft().renderEngine.loadTexture(id, texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
