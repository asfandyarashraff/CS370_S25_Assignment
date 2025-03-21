package edu.cs;  

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/FileUploadServlet")
@MultipartConfig(
    fileSizeThreshold=1024*1024*10,  // 10 MB
    maxFileSize=1024*1024*50,       // 50 MB
    maxRequestSize=1024*1024*100    // 100 MB
)
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
   
    private static final String UPLOAD_DIR = "/var/lib/tomcat9/webapps/uploads"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        File fileSaveDir = new File(UPLOAD_DIR);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }

        // Save the uploaded file
        String fileName = "";
        for (Part part : request.getParts()) {
            fileName = getFileName(part);
            part.write(UPLOAD_DIR + File.separator + fileName);
        }

        // EC2 Elastic IP
        String dbURL = "jdbc:mysql://3.141.101.113:3306/db_repo"
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        String dbUser = "malik";
        String dbPass = "Ahmedyar1!";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
            String sql = "INSERT INTO uploaded_files (file_name, upload_path) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fileName);
            stmt.setString(2, UPLOAD_DIR + File.separator + fileName);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return response
        response.getWriter().write("File uploaded successfully: " + fileName);
    }

    
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return "";
    }
}
