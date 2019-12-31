package cn.smbms.tools;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MutipartFile {
    /**
     * 修改文件名
     * @param suffix 后缀
     * @return
     */
    public static String getNewFileName( String suffix){
        return UUID.randomUUID().toString().replaceAll("-","")+"."+suffix;
    }

    /**
     * 判断文件后缀
     * @param suffix
     * @param suffixs
     * @return
     */
    public static boolean suffixExactness(String suffix,String[] suffixs){
        for (int i =0;i<suffixs.length;i++){
            suffix.equals(suffixs[i]);
            return true;
        }
        return false;
    }

    /**
     * 保存文件
     * @return
     */
    public static boolean keepFile(MultipartFile attach, String path, String fileName){
        File targetFile = new File(path,fileName);
        try {
            attach.transferTo(targetFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
