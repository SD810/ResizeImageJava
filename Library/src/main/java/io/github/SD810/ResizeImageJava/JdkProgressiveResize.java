package io.github.SD810.ResizeImageJava;

import com.mortennobel.imagescaling.MultiStepRescaleOp;

import java.awt.image.BufferedImage;

public class JdkProgressiveResize {

    //https://stackoverflow.com/a/36367652

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imageToScale){
        return new MultiStepRescaleOp(dWidth, dHeight, JdkResize.DEFAULT_INTERPOLATION_TYPE).filter(imageToScale, null);
    }
}
