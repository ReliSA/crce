package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.service.log.LogService;

/**
 *
 * @author kalwi
 */
public class UploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("m_stack: " + (Activator.getStack() != null));
        out.println("m_log: " + (Activator.getLog() != null));
        out.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        
        out.println("--- post ---");
        
        if (ServletFileUpload.isMultipartContent(req)){
            ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
            List fileItemsList;
            try {
                fileItemsList = servletFileUpload.parseRequest(req);
            } catch (FileUploadException e) {
                Activator.getLog().log(LogService.LOG_ERROR, "Exception handling request: " + req.getRequestURL(), e);
                sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            
            for (Object o : fileItemsList) {
                FileItem fi = (FileItem) o;
                
                if (fi.isFormField()) {
                    out.print("name: " + fi.getFieldName());
                    out.println(", string: " + fi.getString());
                } else {
                    String fileName = fi.getName();
                    InputStream is = fi.getInputStream();
                    Activator.getStack().store(fileName, is);
                    is.close();
                    out.println("stored: " + fileName);
                }
            }
        }
        
        out.close();
        
        
//        ServletContext cx = this.getServletContext();
//        
//        String action = req.getParameter("action");
//        
//        if ("send".equals(action)) {
//            String path = req.getPathInfo();
//            if ((path == null) || (path.length() <= 1)) {
//                sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST);
//            }
//            else {
//                String id = path.substring(1);
//                try {
//                    if (m_stack.store(id, req.getInputStream())) {
//                        sendResponse(resp, HttpServletResponse.SC_OK);
//                    }
//                    else {
//                        sendResponse(resp, HttpServletResponse.SC_CONFLICT);
//                    }
//                }
//                catch (IOException e) {
//                    m_log.log(LogService.LOG_WARNING, "Exception handling request: " + req.getRequestURL(), e);
//                    sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                }
//            }
//            
//        }
    }
    
    // send a response with the specified status code
    private void sendResponse(HttpServletResponse response, int statusCode) {
        sendResponse(response, statusCode, "");
    }

    // send a response with the specified status code and description
    private void sendResponse(HttpServletResponse response, int statusCode, String description) {
        try {
            response.sendError(statusCode, description);
        }
        catch (Exception e) {
//            m_log.log(LogService.LOG_WARNING, "Unable to send response with status code '" + statusCode + "'", e);
        }
    }

}