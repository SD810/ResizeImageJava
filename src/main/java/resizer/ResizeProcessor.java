package resizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResizeProcessor {

    /**
     * 지정된 파일 배열을 기반으로 리사이즈를 수행합니다.
     * @param listOfFiles 파일 배열
     * @param dWidth 목표 가로 (px)
     * @param dHeight 목표 세로 (px)
     * @return 리사이즈된 이미지 수
     */
    public static int resizeImagesWithThese(File[] listOfFiles, int dWidth, int dHeight){
        // 파일 갯수
        int numOfTotalFiles = 0;
        int numOfProcessedFiles = 0;
        int numOfNotFiles = 0;
        System.out.println("Total No of File Objects:"+listOfFiles.length);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                // 총 파일 갯수를 셉니다
                numOfTotalFiles++;

                if(listOfFiles[i].canRead() && listOfFiles[i].canWrite()) {
                    // 작업 시작
                    System.out.println("Starting Job : " + listOfFiles[i].getAbsolutePath());
                    try {
                        //이미지 처리
                        BufferedImage imgToScale = ImageIO.read(listOfFiles[i]);
                        BufferedImage scaledImage = resizeImage(dWidth, dHeight, imgToScale);

                        // 경로 및 확장명 따기
                        String pathParent = listOfFiles[i].getParent()+ File.pathSeparator;
                        String extension = getExtension(listOfFiles[i].getAbsolutePath());

                        // 새 파일
                        File newFile = new File(pathParent + listOfFiles[i].getName() + "_Resized" + "." + getFormatName(extension));

                        //파일 쓰기
                        ImageIO.write(scaledImage, getFormatName(extension), newFile);

                        //처리 완료된 파일 수를 셉니다
                        numOfProcessedFiles ++;
                    } catch (IOException ioe) {
                        System.out.println("IOException while resizing: " + listOfFiles[i].getAbsolutePath());
                    }
                }else{
                    System.out.println("File can not be read or written: " + listOfFiles[i].getAbsolutePath());
                }
            }else{
                numOfNotFiles++;
            }
        }
        System.out.println("DONE with "+numOfProcessedFiles+" files from total "+numOfTotalFiles
                +".\nunprocessed: "+(numOfTotalFiles-numOfProcessedFiles)
                +"\nnot files: "+numOfNotFiles);
        return numOfProcessedFiles;
    }

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imgToScale){
        return JdkProgressiveResize.resizeImage(dWidth, dHeight, imgToScale);
    }

    /**
     *  파일 경로에서 확장명을 추출합니다.
     * @param filePath 파일 경로
     * @return
     */
    public static String getExtension(final String filePath){
        int lastIndexOfDot = filePath.lastIndexOf('.');
        if(lastIndexOfDot < 0){
            // 확장자 추출 불가
            return "";
        }
        if(lastIndexOfDot >= filePath.length()){
            // 더 추출할 문자열이 없습니다.
            return "";
        }
        return filePath.substring(lastIndexOfDot+1);
    }

    /**
     * 확장명에 맞는 포맷 형식을 받습니다.
     * @param ext 확장명
     * @return 포맷이름
     */
    public static String getFormatName(final String ext){
        switch(ext){
            case "jpeg":
            case "jpg":
                return "jpg";
            case "gif":
                return "gif";
            default:
            case "png":
                return "png";
        }
    }
}
