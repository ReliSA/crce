package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author kalwi
 */
public class MetafileResourceCreator implements ResourceCreator {
    
    public static final String METAFILE_EXTENSION = ".meta";

    DataModelHelperExt m_dataModelHelper;

    public MetafileResourceCreator() {
        m_dataModelHelper = Activator.getHelper();
    }
    
    @Override
    public void save(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        // TODO check for file protocol
        
        URI mfUri;
        if (uri.toString().toLowerCase().endsWith(METAFILE_EXTENSION)) {
            mfUri = uri;
        } else {
            mfUri = metafileUri(uri);
        }
        
        InputStreamReader reader;
        try {        
            reader = new InputStreamReader(mfUri.toURL().openStream());
        } catch (MalformedURLException e) {
            throw new IOException("Malformed URL in URI: " + mfUri.toString(), e);
        } catch (FileNotFoundException e) {
            return new ResourceImpl();
        }
        
        try {
            return m_dataModelHelper.readMetadata(reader);
        } catch (IOException e) {
            throw new IOException("Can not read XML data", e);
        } catch (Exception e) {
            throw new IOException("Can not parse XML data", e);
        }
    }

    private URI metafileUri(URI uri) throws IllegalStateException {
        try {
            return new URI(uri.toString() + METAFILE_EXTENSION);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unexpected URI syntax: " + uri.toString() + METAFILE_EXTENSION, e);
        }
    }
}
