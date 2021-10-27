package org.zerock.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {

  private static final Logger logger = 
      LoggerFactory.getLogger(UploadFileUtils.class);

//  public static String uploadFile(String uploadPath, 
//      String originalName, 
//      byte[] fileData)throws Exception{
//    
//    return null;
//  }
//  

  
  public static String uploadFile(String uploadPath, 
                              String originalName, 
                              byte[] fileData)throws Exception{
    
    UUID uid = UUID.randomUUID();
    
    String savedName = uid.toString() +"_"+originalName;
    // "uid값 + _ + 파일이름
    
    String savedPath = calcPath(uploadPath);
    
    File target = new File(uploadPath +savedPath,savedName);
    
    FileCopyUtils.copy(fileData, target);
    
    String formatName = originalName.substring(originalName.lastIndexOf(".")+1);
    
    String uploadedFileName = null;
    
    if(MediaUtils.getMediaType(formatName) != null){
      uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
    }else{
      uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
    }
    
    return uploadedFileName;
    
  }
  
  private static  String makeIcon(String uploadPath, 
      String path, 
      String fileName)throws Exception{

    String iconName = 
        uploadPath + path + File.separator+ fileName;
    
    return iconName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  }
  
  
  private static  String makeThumbnail(
              String uploadPath, 
              String path, 
              String fileName)throws Exception{
            
    BufferedImage sourceImg = 
        ImageIO.read(new File(uploadPath + path, fileName));
    
    BufferedImage destImg = 
        Scalr.resize(sourceImg, 
            Scalr.Method.AUTOMATIC, 
            Scalr.Mode.FIT_TO_HEIGHT,100);
    
    String thumbnailName = 
        uploadPath + path + File.separator +"s_"+ fileName;
    
    File newFile = new File(thumbnailName);
    String formatName = 
        fileName.substring(fileName.lastIndexOf(".")+1);
    
    
    ImageIO.write(destImg, formatName.toUpperCase(), newFile);
    return thumbnailName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  } 
  
  
  private static String calcPath(String uploadPath){//일자별로 올라온 파일정리
    // uploadPath = "c:\\zzz\\upload"
    Calendar cal = Calendar.getInstance();
    
    String yearPath = File.separator+cal.get(Calendar.YEAR);
    // "\2021"
    String monthPath = yearPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
    // "\2021\10"
    String datePath = monthPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.DATE));
    // "\2021\1\26" 
    makeDir(uploadPath, yearPath,monthPath,datePath);
    
    logger.info(datePath);
    
    return datePath;
  }
//최종경로 "c:\\zzz\\upload\\2021\\10\\26"
  
  private static void makeDir(String uploadPath, String... paths){
    
    if(new File(paths[paths.length-1]).exists()){
      return;
    }
    
    for (String path : paths) {
      
      File dirPath = new File(uploadPath + path);
      
      if(! dirPath.exists() ){
        dirPath.mkdir();
      } 
    }
  }
  
  
}
