import resizer.ResizeProcessor;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("ResizeImageJava Main");

        String pathname = args[0];
        File folder = new File(pathname);
        File[] listOfFiles = folder.listFiles();

        ResizeProcessor.resizeImagesWithThese(listOfFiles,512,512);
    }
}
