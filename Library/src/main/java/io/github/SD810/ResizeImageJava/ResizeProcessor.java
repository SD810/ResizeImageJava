package io.github.SD810.ResizeImageJava;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResizeProcessor {

    //https://stackoverflow.com/a/36367652
    public static final int RESIZER_JDK_PROGRESSIVE = 1;
    public static final int RESIZER_JDK_DIRECT = 2;
    public static final int RESIZER_LANCOZ = 3;
    public static final int RESIZER_THUMBNAILATOR = 4;

    // 알고리즘 선택용 변수
    public static int resizerAlgorithm = RESIZER_JDK_PROGRESSIVE;

    /**
     * 지정된 파일 배열을 기반으로 리사이즈를 수행합니다.
     * @param listOfFiles 파일 배열
     * @param dWidth 목표 가로 (px)
     * @param dHeight 목표 세로 (px)
     * @param stretch 찌그러뜨림
     * @return 리사이즈된 이미지 수
     */
    public static int resizeImagesWithThese(File[] listOfFiles, int dWidth, int dHeight, boolean stretch){
        // 파일 갯수
        int numOfTotalFiles = 0;
        int numOfProcessedFiles = 0;
        int numOfNotFiles = 0;
        System.out.println("Total No of File Objects:"+listOfFiles.length);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                // 총 파일 갯수를 셉니다
                numOfTotalFiles++;

                if(resizeImageForThis(listOfFiles[i], dWidth, dHeight, stretch)){
                    numOfProcessedFiles++;
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

    /**
     * 지정된 파일 배열을 기반으로 리사이즈를 수행합니다.
     * @param listOfPaths 파일 경로 배열
     * @param dWidth 목표 가로 (px)
     * @param dHeight 목표 세로 (px)
     * @param stretch 찌그러뜨림
     * @return 리사이즈된 이미지 수
     */
    public static int resizeImagesWithThese(String[] listOfPaths, int dWidth, int dHeight, boolean stretch){
        // 파일 갯수
        int numOfTotalFiles = 0;
        int numOfProcessedFiles = 0;
        int numOfNotFiles = 0;
        System.out.println("Total No of File Objects:"+listOfPaths.length);
        for (int i = 0; i < listOfPaths.length; i++) {
            File targetFile = new File(listOfPaths[i]);
            if (targetFile.isFile()) {
                // 총 파일 갯수를 셉니다
                numOfTotalFiles++;

                if(resizeImageForThis(targetFile, dWidth, dHeight, stretch)){
                    numOfProcessedFiles++;
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

    /**
     * 이미지를 크기조정합니다.
     * @param file 이미지 파일
     * @param dWidth 목표 가로 (px)
     * @param dHeight 목표 세로 (px)
     * @param stretch 찌그러뜨림
     * @return
     */
    public static boolean resizeImageForThis(File file, int dWidth, int dHeight, boolean stretch){
        if(file.canRead() && file.canWrite()) {
            String extension = getExtension(file.getAbsolutePath());
            if(checkIfSupportedFiles(extension)){
                // 작업 시작
                System.out.println("Starting Job : " + file.getAbsolutePath());
                try {

                    //이미지 처리
                    BufferedImage imgToScale = ImageIO.read(file);

                    BufferedImage scaledImage = resizeImage(dWidth, dHeight, imgToScale, stretch);

                    // 경로 및 확장명 따기
                    String fileNameWithExt = file.getName();
                    String pathParent = getCurrentDirectory(file.getAbsolutePath());

                    System.out.println(pathParent);

                    //기존 파일 이름바꾸기로 original 처리
                    if(file.renameTo(new File(pathParent + fileNameWithExt + ".original_image"))) {
                        //이름을 바꿨다면 그 자리에 리사이징된 이미지를 저장할 파일을 만듭니다.

                        // 새 파일
                        File newFile = new File(pathParent + fileNameWithExt);

                        //파일 쓰기
                        ImageIO.write(scaledImage, getFormatName(extension), newFile);

                        //처리 완료.
                        return true;
                    }else{
                        //파일을 이동할 수 없음
                        System.out.println("cannot rename file: " + file.getAbsolutePath());
                    }
                } catch (IOException ioe) {
                    // IOException 발생
                    System.out.println("IOException while resizing: " + file.getAbsolutePath());
                }
            }else{
                // 미지원 파일
                System.out.println("File is unsupported: " + file.getAbsolutePath());
            }
        }else{
            // 파일을 읽고 쓸 수 없음
            System.out.println("File can not be read or written: " + file.getAbsolutePath());
        }
        return false;
    }

    /**
     * Image resize
     * @param dWidth 목표 가로
     * @param dHeight 목표 세로
     * @param imgToScale 크기조절할 이미지
     * @param stretch 찌그러뜨림 여부
     * @return
     */
    public static BufferedImage resizeImage(int dWidth, int dHeight, BufferedImage imgToScale, boolean stretch){
        if(stretch) {
            return resizeImageProc(dWidth, dHeight, imgToScale);
        }else{
            Point dimenDest = getResizedDimensions(imgToScale, dWidth, dHeight);
            return resizeImageProc(dimenDest.x, dimenDest.y, imgToScale);
        }
    }

    /**
     * 리사이즈 처리 로직
     * 선택된 알고리즘에 따라 리사이즈 진행
     * 기본값은 JDK_PROGRESSIVE
     * @param targetWidth 대상 가로 (px)
     * @param targetHeight 대상 세로 (px)
     * @param imgToScale 리사이즈할 이미지
     * @return
     */
    private static BufferedImage resizeImageProc(int targetWidth, int targetHeight, BufferedImage imgToScale){
        switch(resizerAlgorithm){
            default:
            case RESIZER_JDK_PROGRESSIVE:
                return JdkProgressiveResize.resizeImage(targetWidth,targetHeight, imgToScale);
            case RESIZER_JDK_DIRECT:
                return JdkResize.resizeImage(targetWidth, targetHeight, imgToScale);
            case RESIZER_LANCOZ:
                return LancozResize.resizeImage(targetWidth, targetHeight, imgToScale);
            case RESIZER_THUMBNAILATOR:
                return ThumbnailatorResize.resizeImage(targetWidth, targetHeight, imgToScale);
        }
    }

    /**
     *  파일 경로에서 확장명을 추출합니다.
     * @param filePath 파일 경로
     * @return
     */
    private static String getExtension(final String filePath){
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
    private static String getFormatName(String ext){
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
    private static String getCurrentDirectory(final String filePath){
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

    /**
     * 리사이즈 될 크기를 계산합니다.
     *
     * @param image 이미지
     * @param dWidth 목표 가로 (px)
     * @param dHeight 목표 세로(px)
     * @return Point 객체 x가 교정된 가로, y가 교정된 세로
     */
    private static Point getResizedDimensions(BufferedImage image, final int dWidth, final int dHeight){
        return getResizedDimensions(image.getWidth(),image.getHeight(),dWidth,dHeight);
    }

    /**
     * 리사이즈 될 크기를 계산합니다.
     *
     * @param originalWidth 원래 가로 (px)
     * @param originalHeight 원래 세로 (px)
     * @param destinationWidth 목표 가로 (px)
     * @param destinationHeight 목표 세로(px)
     * @return Point 객체 x가 교정된 가로, y가 교정된 세로
     */
    public static Point getResizedDimensions(final int originalWidth, final int originalHeight, final int destinationWidth, final int destinationHeight){
        Point dimen = new Point(0,0);

        final int oWidth = Math.abs(originalWidth);
        final int oHeight = Math.abs(originalHeight);
        final int dWidth = Math.abs(destinationWidth);
        final int dHeight = Math.abs(destinationHeight);

        System.out.println("original width and height "+ oWidth + " x "+ oHeight);

        int destAspectType;

        if (dWidth > dHeight) {
            destAspectType = ASPECT_TYPE_WIDTH_LONG;
        } else if (dWidth < dHeight) {
            destAspectType = ASPECT_TYPE_HEIGHT_LONG;
        } else {
            destAspectType = ASPECT_TYPE_SAME_WH;
        }

        int aspectType;
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
                    dimen.y = (int)Math.round ((double)dWidth * aspectRatioReversed);
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
                    dimen.x = (int)Math.round ((double)dHeight * aspectRatioReversed);
                    dimen.y = dHeight;
                    break;
            }

        } else {
            final double aspectRatioWH =  ((double) oWidth / (double) oHeight);
            final double aspectRatioHW =  ((double) oHeight / (double) oWidth);

            //final double destAspectRatioWH =  ((double) dWidth / (double) dHeight);
            //final double destAspectRatioHW =  ((double) dHeight / (double) dWidth);

            //final double origWidth = (double)oWidth;
            //final double origHeight = (double)oHeight;
            final double destWidth = (double)dWidth;
            final double destHeight = (double)dHeight;

            double calculatedWidth = 0.;
            double calculatedHeight = 0.;

            // 목적 종횡비가 가로가 긴 경우
            switch (aspectType) {
            case ASPECT_TYPE_WIDTH_LONG:
                //가로가 깁니다. 세로는 가로 * 종횡비 입니다.
                calculatedWidth = destWidth;
                calculatedHeight = destWidth * aspectRatioHW;
                if(calculatedHeight > destHeight){
                    // 세로가 삐져나간 경우 가로를 재조정합니다.
                    calculatedWidth = destHeight * aspectRatioWH;
                    calculatedHeight = destHeight;
                }
                break;
            default:
            case ASPECT_TYPE_SAME_WH:
                // 가로세로가 같습니다.
                if(destAspectType == ASPECT_TYPE_WIDTH_LONG) {
                    // 목적 종횡비가 가로가 긴 경우
                    calculatedWidth = destHeight;
                    calculatedHeight = destHeight;
                } else {
                    // 목적 종횡비가 세로가 긴 경우
                    calculatedWidth = destWidth;
                    calculatedHeight = destWidth;
                }
                break;
            case ASPECT_TYPE_HEIGHT_LONG:
                // 세로가 깁니다. 가로는 세로 * 종횡비 입니다.
                calculatedWidth = destHeight * aspectRatioWH;
                calculatedHeight = destHeight;
                if(calculatedWidth > destWidth){
                    //가로가 삐져나간 경우 세로를 재조정합니다.
                    calculatedWidth = destWidth;
                    calculatedHeight = destWidth * aspectRatioHW;
                }
                break;
            }

            dimen.x = (int)Math.round(calculatedWidth);
            dimen.y = (int)Math.round(calculatedHeight);
        }
        //0으로 리사이징 되지 않게 1px를 최소값으로 합니다
        if(dimen.x <= 0){
            dimen.x = 1;
        }
        if(dimen.y <= 0){
            dimen.y = 1;
        }
        System.out.println("desired width and height within "+dWidth + " x "+ dHeight +" : "+dimen.x+" x "+ dimen.y);

        return dimen;
    }
}
