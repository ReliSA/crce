package cz.zcu.kiv.crce.metadata.service;

import java.net.URI;
import java.util.List;
import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface MetadataService {

    @Nonnull
    String getIdentityNamespace();

    @Nonnull
    String getPresentationName(@Nonnull Resource resource);

    void setPresentationName(@Nonnull Resource resource, @Nonnull String name);

    @Nonnull
    String getPresentationName(@Nonnull Capability capability);

    @Nonnull
    String getPresentationName(@Nonnull Requirement requirement);

    @Nonnull
    String getPresentationName(@Nonnull Property property);

    @Nonnull
    String getPresentationName(@Nonnull Attribute<?> attribute);

    @Nonnull
    URI getUri(@Nonnull Resource resource);

    @Nonnull
    URI getRelativeUri(@Nonnull Resource resource);

    void setUri(@Nonnull Resource resource, @Nonnull URI uri);

    @Nonnull
    String getFileName(@Nonnull Resource resource);

    void setFileName(@Nonnull Resource resource, @Nonnull String fileName);

    long getSize(@Nonnull Resource resource);

    void setSize(@Nonnull Resource resource, long size);

    @Nonnull
    List<String> getCategories(@Nonnull Resource resource);

    void addCategory(@Nonnull Resource resource, @Nonnull String category);

    void removeCategory(@Nonnull Resource resource, @Nonnull String category);
}
