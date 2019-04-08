package resizer;

import javax.imageio.ImageIO;
import java.awt.*;
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
                    String extension = getExtension(listOfFiles[i].getAbsolutePath());
                    if(checkIfSupportedFiles(extension)){
                        // 작업 시작
                        System.out.println("Starting Job : " + listOfFiles[i].getAbsolutePath());
                        try {
                            //이미지 처리
                            BufferedImage imgToScale = ImageIO.read(listOfFiles[i]);

                            BufferedImage scaledImage = resizeImage(dWidth, dHeight, imgToScale, false);

                            // 경로 및 확장명 따기
                            String pathParent = getCurrentDirectory(listOfFiles[i].getAbsolutePath());

                            System.out.println(pathParent);
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
                        System.out.println("File is unsupported: " + listOfFiles[i].getAbsolutePath());
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

    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imgToScale, boolean stretch){
        if(stretch) {
            return JdkProgressiveResize.resizeImage(dWidth, dHeight, imgToScale);
        }else{
            Point dimenDest = getResizedDimensions(imgToScale, dWidth, dHeight);
            return JdkProgressiveResize.resizeImage(dimenDest.x, dimenDest.y, imgToScale);
        }
    }

    /**
     *  파일 경로에서 확장명을 추출합니다.
     * @param filePath 파일 경로
     * @return
     */
    public static String getExtension(final String filePath){
        if(filePath == null){
            //파일경로 null 추출 불가
            return "";
        }
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
    public static String getFormatName(String ext){
        ext = ext.toLowerCase();
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

    /**
     * 지원 파일인지 확인합니다.
     *
     * @param ext 확장명
     * @return jpg/jpeg gif png 세가지만 true
     */
    public static boolean checkIfSupportedFiles(String ext){
        ext = ext.toLowerCase();
        switch(ext){
            case"jpeg":
            case"jpg":
            case"gif":
            case"png":
                return true;
        }
        return false;
    }

    /**
     * 소속 디렉토리 경로를 슬래시까지 포함하여 보내줍니다.
     * @param filePath 파일 전체 경로
     * @return 파일의 소속 디렉토리 경로
     */
    public static String getCurrentDirectory(final String filePath){
        if(filePath == null){
            return "";
        }
        char saperator = '/';
        int lastIndexOfSeparator = filePath.lastIndexOf(saperator);
        if(lastIndexOfSeparator < 0){
            // 재시도
            saperator = '\\';
            lastIndexOfSeparator = filePath.lastIndexOf(saperator);
        }
        if(lastIndexOfSeparator < 0){
            //마지막 분리자 추출 불가
            return filePath;
        }

        return filePath.substring(0,lastIndexOfSeparator)+saperator;
    }

    private static final int ASPECT_TYPE_WIDTH_LONG = 1; // 가로가 더 김
    private static final int ASPECT_TYPE_SAME_WH = 0; // 가로세로 같음
    private static final int ASPECT_TYPE_HEIGHT_LONG = -1; // 세로가 더 김

    public static Point getResizedDimensions(BufferedImage image, final int dWidth, final int dHeight){
        Point dimen = new Point(dWidth,dHeight);
        int oWidth = image.getWidth();
        int oHeight = image.getHeight();

        System.out.println("original width and height "+ oWidth + " x "+ oHeight);

        int destAspectType = ASPECT_TYPE_SAME_WH;

        if (dWidth > dHeight) {
            destAspectType = ASPECT_TYPE_WIDTH_LONG;
        } else if (dWidth < dHeight) {
            destAspectType = ASPECT_TYPE_HEIGHT_LONG;
        } else {
            destAspectType = ASPECT_TYPE_SAME_WH;
        }

        int aspectType = 0;
        if (oWidth > oHeight) {
            aspectType = ASPECT_TYPE_WIDTH_LONG;
        } else if (oWidth < oHeight) {
            aspectType = ASPECT_TYPE_HEIGHT_LONG;
        } else {
            aspectType = ASPECT_TYPE_SAME_WH;
        }


        if(destAspectType == ASPECT_TYPE_SAME_WH) {


            double aspectRatioReversed = 1.;
            switch (aspectType) {
                case ASPECT_TYPE_WIDTH_LONG:
                    //가로가 깁니다. 세로는 가로 * 종횡비 입니다.
                    aspectRatioReversed = ((double) oHeight / (double) oWidth);
                    dimen.x = dWidth;
                    dimen.y = (int) ((double)dWidth * aspectRatioReversed);
                    break;
                default:
                case ASPECT_TYPE_SAME_WH:
                    // 가로세로가 같습니다.
                    dimen.x = dWidth;
                    dimen.y = dHeight;
                    break;
                case ASPECT_TYPE_HEIGHT_LONG:
                    // 세로가 깁니다. 가로는 세로 * 종횡비 입니다.
                    aspectRatioReversed = ((double) oWidth / (double) oHeight);
                    dimen.x = (int) ((double)dHeight * aspectRatioReversed);
                    dimen.y = dHeight;
                    break;
            }

        } else {
            final double aspectRatioWH =  ((double) oWidth / (double) oHeight);
            final double aspectRatioHW =  ((double) oHeight / (double) oWidth);

            final double destAspectRatioWH =  ((double) dWidth / (double) dHeight);
            final double destAspectRatioHW =  ((double) dHeight / (double) dWidth);

            final double origWidth = (double)oWidth;
            final double origHeight = (double)oHeight;
            final double destWidth = (double)dWidth;
            final double destHeight = (double)dHeight;

            double calculatedWidth = 0.;
            double calculatedHeight = 0.;

            if(destAspectType == ASPECT_TYPE_WIDTH_LONG){
                // 목적 종횡비가 가로가 긴 경우
                switch (aspectType) {
                    case ASPECT_TYPE_WIDTH_LONG:
                        //가로가 깁니다. 세로는 가로 * 종횡비 입니다.
                        calculatedWidth = destWidth;
                        calculatedHeight = destWidth * aspectRatioHW;

                        if(calculatedHeight > destHeight){
                            // 초과된 경우
                            calculatedWidth = destHeight * aspectRatioWH;
                            calculatedHeight = destHeight;
                        }
                        break;
                    default:
                    case ASPECT_TYPE_SAME_WH:
                        // 가로세로가 같습니다.
                        break;
                    case ASPECT_TYPE_HEIGHT_LONG:
                        // 세로가 깁니다. 가로는 세로 * 종횡비 입니다.
                        break;
                }
            }else{// if(destAspectType == ASPECT_TYPE_HEIGHT_LONG){
                // 목적 종횡비가 세로가 긴 경우
                switch (aspectType) {
                    case ASPECT_TYPE_WIDTH_LONG:
                        //가로가 깁니다. 세로는 가로 * 종횡비 입니다.
                        break;
                    default:
                    case ASPECT_TYPE_SAME_WH:
                        // 가로세로가 같습니다.
                        break;
                    case ASPECT_TYPE_HEIGHT_LONG:
                        // 세로가 깁니다. 가로는 세로 * 종횡비 입니다.
                        calculatedWidth = destHeight * aspectRatioWH;
                        calculatedHeight = destHeight;

                        if(calculatedWidth > destWidth){
                            // 초과된 경우
                            calculatedWidth = destWidth;
                            calculatedHeight = destWidth * aspectRatioHW;
                        }
                        break;
                }
            }

            dimen.x = (int) calculatedWidth;
            dimen.y = (int) calculatedHeight;
        }
        System.out.println("desired width and height within "+dWidth + " x "+ dHeight +" : "+dimen.x+" x "+ dimen.y);

        return dimen;
    }
}
