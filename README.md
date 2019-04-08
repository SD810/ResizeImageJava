# ResizeImageJava
이미지 리사이징 기능 구현체
from: https://stackoverflow.com/a/36367652

## dependencies
put this in project's "libs" folder as described in fileTree
```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.mortennobel:java-image-scaling:0.8.6'
    implementation 'net.coobird:thumbnailator:0.4.8'
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}
```

## Usage
```Java
// listOfFiles : File[] or String[]
// targetWidth/targetHeight : int, pixel
// doStretch : boolean for ignoring aspect ratio
ResizeProcessor.resizeImagesWithThese(listOfFiles, targetWidth, targetHeight, doStretch);
```

```Java
// file : File
// targetWidth/targetHeight : int, pixel
// doStretch : boolean for ignoring aspect ratio
ResizeProcessor.resizeImageForThis(file, targetWidth, targetHeight, doStretch);
```

## Change Algorithm
set into
```Java
ResizeProcessor.resizerAlgorithm = ResizeProcessor.RESIZER_LANCOZ;
```
pick from these
```Java
public static final int RESIZER_JDK_PROGRESSIVE = 1;
public static final int RESIZER_JDK_DIRECT = 2;
public static final int RESIZER_LANCOZ = 3;
public static final int RESIZER_THUMBNAILATOR = 4;
```
