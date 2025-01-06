package com.ispan.hestia.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.exception.BadRequestException;


public class ProfileUtil {
	
	// user頭像大小限制 => 3MB
	public static final long MAX_SIZE = 3 * 1024 * 1024;
    
    
    // 允許的檔案類型
    public static final List<String> ALLOWED_FORMATS = Arrays.asList("gif", "jpg", "png", "jpeg", "webp");
    public static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/gif", "image/jpeg", "image/png", "image/webp");

    /**
     * 驗證檔案大小
     */
    public static void validateFileSize(MultipartFile file) {
        if (file != null && file.getSize() > MAX_SIZE) {
            throw new BadRequestException("檔案過大無法上傳");
        }
    }

    /**
     * 驗證檔案格式
     */
    public static void validateFileFormat(MultipartFile file) {
        String fileType = getExtensionName(file.getOriginalFilename());
        if (file != null && fileType != null && !ALLOWED_FORMATS.contains(fileType.toLowerCase())) {
            throw new BadRequestException("檔案格式錯誤！僅支持 ，" + ALLOWED_FORMATS + " 格式");
        }
    }
    
    /**
     * 驗證 MIME 類型
     */
    public static void validateMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (file == null || mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new BadRequestException("檔案格式錯誤！僅支持 ，" + ALLOWED_FORMATS + " 格式");
        }
    }
    
    /**
     * 截取副檔名(不帶 .)
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
}