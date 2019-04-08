package io.github.SD810.ResizeImageJava;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JdkResize {

    // https://stackoverflow.com/a/36367652

    public static final Object DEFAULT_INTERPOLATION_TYPE = RenderingHints.VALUE_INTERPOLATION_BILINEAR;

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imageToScale) {
        int type = (imageToScale.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        BufferedImage scaledImage = new BufferedImage(dWidth, dHeight, type);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, DEFAULT_INTERPOLATION_TYPE);
        graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
        graphics2D.dispose();
        return scaledImage;
    }
}
