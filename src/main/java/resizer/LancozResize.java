package resizer;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import java.awt.image.BufferedImage;

public class LancozResize {

    //https://stackoverflow.com/a/36367652

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imageToScale){
        ResampleOp resizeOp = new ResampleOp(dWidth, dHeight);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        BufferedImage scaledImage = resizeOp.filter(imageToScale, null);
        return scaledImage;
    }
}
