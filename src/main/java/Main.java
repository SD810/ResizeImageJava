import resizer.ResizeProcessor;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("ResizeImageJava Main");

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
            int processedFiles = ResizeProcessor.resizeImagesWithThese(listOfFiles, 512, 512);

            System.out.println(processedFiles +"has been processed");
        }
    }
}
