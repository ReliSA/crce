package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.util.Map;
import org.osgi.framework.Version;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Resource extends PropertyProvider<Resource> {

    String getId();

    String getSymbolicName();

    Version getVersion();

    String getPresentationName();

    URI getUri();
    
    URI getRelativeUri();

    /**
     * Returns the resource size in bytes or -1 if size is unknown.
     * @return the resource size.
     */
    long getSize();

    String[] getCategories();

    Capability[] getCapabilities();

    Capability[] getCapabilities(String name);

    Requirement[] getRequirements();

    Requirement[] getRequirements(String name);
    
    Map<String, String> getPropertiesMap();

    boolean hasCategory(String category);

    boolean hasCapability(Capability capability);

    boolean hasRequirement(Requirement requirement);


    void setSymbolicName(String name);
    
    void setSymbolicName(String name, boolean isStatic);

    void setPresentationName(String name);

    void setVersion(Version version);
    
    void setVersion(Version version, boolean isStatic);

    void setVersion(String version);
    
    void setVersion(String version, boolean isStatic);

    void addCategory(String category);

    void addCapability(Capability capability);

    void addRequirement(Requirement requirement);

    Capability createCapability(String name);

    Requirement createRequirement(String name);

    void unsetCategory(String category);
    
    void unsetCapability(Capability capability);
    
    void unsetRequirement(Requirement requirement);
    
    
    /**
     * Sets resource size.
     * @param size size in bytes to set.
     */
    void setSize(long size);

    void setUri(URI uri);

    boolean isWritable();
    
    void unsetWritable();
    
    /**
     * Tells whether or not the version of this resource is hard-coded in
     * artifact's binary data (e.g. in bundle manifest).
     * @return <code>true</code> if the version is hard-coded and can not be
     * changed.
     */
    boolean isVersionStatic();
    
    /**
     * Tells whether or not the symbolic name of this resource is hard-coded in
     * artifact's binary data (e.g. in bundle manifest).
     * @return <code>true</code> if the symbolic name is hard-coded and can not
     * be changed.
     */
    boolean isSymbolicNameStatic();
    
    String asString();
}