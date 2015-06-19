package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.webservices.indexer.WebservicesDescription;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet closely corresponds to "Web services" section in Web UI and it's "webservices.jsp" template. It uses buffer into which parsed artifacts of
 * webservices represented by IDLs can be put and from which they can consequently be commited into store.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebservicesServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(WebservicesServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean failed = false;

        if (req.getParameter("uri") != null) {
            Buffer buffer = Activator.instance().getWsBuffer(req);
            try {
                buffer.commit(true);
            } catch (IOException e) {
                logger.error("Could not commit", e);
                failed = true;
            }
        } else {
            failed = true;
        }
        
        if (failed) {
            logger.error("Commit failed");
            ResourceServlet.setError(req.getSession(), !failed, "Commit failed");
        } else {
            req.getRequestDispatcher("resource?link=webservices");
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // process HTTP POST parameter with uri of Webservice IDL
        String uri;
        boolean upload_success = true;
        if (req.getParameter("uri") != null && req.getParameter("uri").length() > 0) {
            uri = req.getParameter("uri");
            logger.debug("Got \"uri\" parameter with value \"{}\".", uri);
            
            // invoke processing of remote IDL document
            WebservicesDescription wd = Activator.instance().getWebservicesDescription();
            List<Resource> resources = wd.createWebserviceRepresentations(uri);

            // save all returned resources into buffer
            if (resources == null) {
                logger.warn("Could not parse web services IDL at \"{}\" into CRCE artifact.", uri);
            } else {
                for (Resource resource : resources) {
                    if (resource == null) {
                        logger.warn("Could not parse web service IDL at \"{}\" into CRCE artifact.", uri);
                    } else {
                        logger.info("Web service IDL at \"{}\" was successfully parsed into CRCE artifact.", uri);

                        // save CRCE resource into repository
                        try {
                            Activator.instance().getWsBuffer(req).put(uri, createInputStreamFromIdlUri(uri),resource);
                        } catch (RefusedArtifactException ex) {
                            logger.warn("Artifact revoked: ", ex.getMessage());
                            upload_success = false;
                        }
                    }
                }
            }
        } else {
            logger.debug("Empty \"uri\" parameter during HTTP POST.");
        }
        
        // redirect to Webservices section
        ResourceServlet.setError(req.getSession(), upload_success, upload_success ? "Upload was succesful." : "Upload failed.");
        req.getSession().setAttribute("source", "webservices");
        req.getRequestDispatcher("resource?link=webservices").forward(req, resp);
    }
    
    /**
     * Opens remote URL of IDL document and returns {@link java.io.InputStream} of that location in order to read content from it.
     * 
     * @param url Remote URL
     * @return {@link java.io.InputStream} of passed <code>url</code> location.
     */
    private InputStream createInputStreamFromIdlUri(String url) {
        
        // try to access IDL content at uri
        logger.debug("Attempting to access IDL at \"{}\".", url);
        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException ex) {
            logger.error("MalformedURLException: {}", url, ex);
        }
        if (urlObj == null) {
            return null;
        }
        
        // try to return InputStream
        try {
            return urlObj.openStream();
        } catch (IOException ex) {
            logger.error("IOException: {}", url, ex);
            return null;
        }
    }
}
