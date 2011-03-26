package cz.zcu.kiv.crce.handler.versioning.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;

/**
 * This plugin adds the suffix to the version qualifier of the resource uploaded
 * to the buffer or put to the store, if the target repository already contains
 * a resource with the same symbolic name and version.
 * 
 * <p>The suffix is '_x' where <i>x</i> is a number. If there still is a
 * resource with the same symbolic name and version (now with the suffix), then
 * the number in the suffix is increased by one. The first number is 2.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class IncreaseVersionActionHandler extends AbstractActionHandler implements ActionHandler {

    private volatile LogService m_log;

    @Override
    public Resource onPutToStore(Resource resource, Store store) throws RevokedArtifactException {
        if (resource.isVersionStatic()) {
            return resource;
        }

        Repository repository = store.getRepository();
        Version version = resource.getVersion();
        for (int i = 2; repository.contains(resource); i++) {
            resource.setVersion(new Version(version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier() + "_" + i));
            if (resource.getVersion().equals(version)) {
                m_log.log(LogService.LOG_WARNING, "Can not change resource's version but the version is not static: " + resource.getId() + ", version: " + resource.getVersion());
                return resource;
            }
        }

        return resource;
    }

    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        return super.onUploadToBuffer(resource, buffer, name); // TODO
    }
}