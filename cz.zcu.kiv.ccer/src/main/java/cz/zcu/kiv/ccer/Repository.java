package cz.zcu.kiv.ccer;

import java.net.URL;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public interface Repository {

    public void put(URL resource);

    public Resource[] get(String filter);
    
    public URL getNewestVersion(String symbolicName);  // ??
    
    public boolean isCompatible(Resource resource);
    
}
