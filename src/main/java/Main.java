import io.github.SD810.ResizeImageJava.ResizeProcessor;

import java.awt.*;
import java.io.File;

public class Main {

    private static final boolean TEST_MODE = false;
    private static final Point[] DIMENS = {new Point(400,300), new Point(300,300), new Point(300, 400)};


    public static void main(String[] args) {
        System.out.println("ResizeImageJava Main");


        if(TEST_MODE){
            final int dWidth = 100;
            final int dHeight = 50;
            System.out.println("TEST MODE BEGIN for w:"+ dWidth+" x h:"+dHeight);
            for (Point dimen : DIMENS){
                Point result = ResizeProcessor.getResizedDimensions(dimen.x,dimen.y,dWidth,dHeight);
                System.out.println("from- w:"+dimen.x+" x h:"+dimen.y+" // to- w:"+result.x+" x h:"+result.y);
            }
            System.out.println("TEST MODE ENDS");
            return ;
        }

        if(args.length <= 0){
            System.out.println("no args provided. program will exit.");
            return ;
        }
        String pathname = args[0];

        if(pathname == null || pathname.length() <= 1){
            System.out.println("for safety reason, you cannot process root.  program will exit.");
            return ;
        } else {
            File folder = new File(pathname);
            if( !
                    (folder.getAbsolutePath().contains("Pictures")
                            && folder.getAbsolutePath().contains("Screenshots"))
            ){
                System.out.println("Only works for customed location, for testing.  program will exit.");
                return ;
            }

            File[] listOfFiles = folder.listFiles();
            int processedFiles = ResizeProcessor.resizeImagesWithThese(listOfFiles, 512, 512, false, false);

            System.out.println(processedFiles +" files has been processed");
        }
    }
}
