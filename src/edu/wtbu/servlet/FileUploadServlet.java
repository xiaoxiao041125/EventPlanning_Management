package edu.wtbu.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.alibaba.fastjson.JSONObject;

/**
 * 文件上传Servlet
 * 用于处理前端上传的图片和附件文件
 */
@WebServlet("/FileUpload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB - 超过此大小将写入临时文件
    maxFileSize = 1024 * 1024 * 10,       // 10MB - 单个文件最大大小
    maxRequestSize = 1024 * 1024 * 50     // 50MB - 整个请求最大大小
)
public class FileUploadServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // 允许上传的文件类型
    private static final String[] ALLOWED_FILE_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    };
    
    // 允许的文件扩展名
    private static final String[] ALLOWED_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".webp",
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"
    };

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置响应编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();
        
        try {
            // 获取上传的文件
            Part filePart = request.getPart("file");
            
            if (filePart == null || filePart.getSize() == 0) {
                result.put("flag", "fail");
                result.put("data", "请选择要上传的文件");
                out.print(result.toJSONString());
                return;
            }
            
            // 获取原始文件名
            String originalFileName = getSubmittedFileName(filePart);
            
            // 检查文件类型
            String fileType = filePart.getContentType();
            if (!isAllowedFileType(fileType)) {
                result.put("flag", "fail");
                result.put("data", "不支持的文件类型: " + fileType);
                out.print(result.toJSONString());
                return;
            }
            
            // 检查文件扩展名
            String fileExtension = getFileExtension(originalFileName);
            if (!isAllowedExtension(fileExtension)) {
                result.put("flag", "fail");
                result.put("data", "不支持的文件格式: " + fileExtension);
                out.print(result.toJSONString());
                return;
            }
            
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
            
            // 获取项目的源目录作为上传路径
            String projectPath = System.getProperty("user.dir");
            String uploadPath = projectPath + "\\WebContent\\upload";
            File uploadDir = new File(uploadPath);
            
            // 打印上传路径到控制台
            System.out.println("Project path: " + projectPath);
            System.out.println("Upload path: " + uploadPath);
            
            // 如果目录不存在，创建目录
            if (!uploadDir.exists()) {
                System.out.println("Creating upload directory: " + uploadPath);
                uploadDir.mkdirs();
                System.out.println("Upload directory created: " + uploadDir.exists());
            }
            
            // 构建完整的文件保存路径
            String filePath = uploadPath + File.separator + uniqueFileName;
            
            // 保存文件到服务器
            filePart.write(filePath);
            
            // 打印文件保存路径
            System.out.println("File saved to: " + filePath);
            
            // 构建文件访问URL
            String fileUrl = request.getContextPath() + "/upload/" + uniqueFileName;
            
            // 返回成功结果
            result.put("flag", "success");
            result.put("data", "文件上传成功");
            result.put("fileName", originalFileName);
            result.put("fileUrl", fileUrl);
            result.put("fileSize", filePart.getSize());
            result.put("fileType", fileType);
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("flag", "error");
            result.put("data", "文件上传失败: " + e.getMessage());
        }
        
        out.print(result.toJSONString());
        out.flush();
        out.close();
    }
    
    /**
     * 获取上传的文件名
     */
    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) {
            return null;
        }
        
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                // 处理IE浏览器会带完整路径的问题
                int lastSlash = fileName.lastIndexOf('\\');
                if (lastSlash != -1) {
                    fileName = fileName.substring(lastSlash + 1);
                }
                return fileName;
            }
        }
        return null;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
    
    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String fileType) {
        if (fileType == null) {
            return false;
        }
        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (allowedType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查文件扩展名是否允许
     */
    private boolean isAllowedExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
