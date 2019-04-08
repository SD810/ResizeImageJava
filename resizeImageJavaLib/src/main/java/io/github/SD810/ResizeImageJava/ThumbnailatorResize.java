package io.github.SD810.ResizeImageJava;

import net.coobird.thumbnailator.makers.FixedSizeThumbnailMaker;
import net.coobird.thumbnailator.resizers.DefaultResizerFactory;
import net.coobird.thumbnailator.resizers.Resizer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ThumbnailatorResize {

    //https://stackoverflow.com/a/36367652

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imageToScale){
        Resizer resizer = DefaultResizerFactory.getInstance().getResizer(
                new Dimension(imageToScale.getWidth(), imageToScale.getHeight()),
                new Dimension(dWidth, dHeight));
        BufferedImage scaledImage = new FixedSizeThumbnailMaker(
                dWidth, dHeight, false, true).resizer(resizer).make(imageToScale);
        return scaledImage;
    }
}
