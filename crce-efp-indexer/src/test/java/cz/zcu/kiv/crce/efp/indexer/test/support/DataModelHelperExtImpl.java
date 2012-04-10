package cz.zcu.kiv.crce.efp.indexer.test.support;

import cz.zcu.kiv.crce.results.Result;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import org.apache.felix.bundlerepository.impl.CapabilityImpl;
import org.apache.felix.bundlerepository.impl.RequirementImpl;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import org.apache.felix.bundlerepository.impl.PullParser;
import org.apache.felix.bundlerepository.impl.RepositoryParser;
import org.apache.felix.bundlerepository.impl.ResourceImpl;
import org.apache.felix.bundlerepository.impl.XmlWriter;
import org.kxml2.io.KXmlParser;
import org.osgi.framework.Version;
import org.xmlpull.v1.XmlPullParser;

import static org.apache.felix.bundlerepository.Resource.*;

/**
 * Implementation of <code>DataModelHelperExt</code>
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class DataModelHelperExtImpl implements DataModelHelperExt {
    
    private ResourceCreator m_resourCreator=new ResourceCreatorImpl();

    @Override
    public Result readResult(String xml) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result readResult(Reader reader) throws IOException, Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String writeResult(Result result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeResult(Result result, Writer writer) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository readRepository(String xml) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository readRepository(Reader reader) throws IOException, Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String writeRepository(Repository repository) {
        try {
            StringWriter sw = new StringWriter();
            writeRepository(repository, sw);
            return sw.toString();
        } catch (IOException e) {
            IllegalStateException ex = new IllegalStateException(e);
            ex.initCause(e);
            throw ex;
        }
    }

    @Override
    public void writeRepository(Repository repository, Writer writer) throws IOException {
        XmlWriter w = new XmlWriter(writer);
        toXml(w, repository);
    }

    @Override
    public String writeMetadata(Resource resource) {
        try {
            StringWriter sw = new StringWriter();
            writeMetadata(resource, sw);
            return sw.toString();
        } catch (IOException e) {
            IllegalStateException ex = new IllegalStateException(e); // XXX
            ex.initCause(e);
            throw ex;
        }
    }

    @Override
    public void writeMetadata(Resource resource, Writer writer) throws IOException {
        XmlWriter w = new XmlWriter(writer);

        w.element(OBR);

        toXml(w, resource, true);
        
        w.end();

    }

//    @Override
//    public ResourceImpl createResource(URL bundleUrl) throws IOException {
//        ResourceImpl resource = null;
//
//        try {
//            resource = (ResourceImpl) super.createResource(bundleUrl);
//            resource.addCategory("osgi");
//        } catch (IllegalArgumentException e) {
//            // not a bundle
//            // TODO - atach some other pluginable resource creators, e.g. for CoSi bundles
//            return null;
//        } catch (NullPointerException e) {
//            return null;
//        }
//
//        return resource;
//    }

    @Override
    public Resource readMetadata(String xml) throws IOException, Exception {
        try {
            return readMetadata(new StringReader(xml));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Resource readMetadata(Reader reader) throws IOException, Exception {
        XmlPullParser parser = new KXmlParser();
        parser.setInput(reader);

        int event = parser.nextTag();
        if (event != XmlPullParser.START_TAG || !OBR.equals(parser.getName())) {
            throw new Exception("Expected element " + OBR);
        }

        Resource out = m_resourCreator.createResource();
        ResourceImpl felixResource = parseMetadata(parser);
        
        for (org.apache.felix.bundlerepository.Capability fcap : felixResource.getCapabilities()) {
            Capability cap = m_resourCreator.createCapability(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                cap.setProperty(fprop.getName(), fprop.getValue(), Type.getValue(fprop.getType()));
            }
            out.addCapability(cap);
        }
        for (org.apache.felix.bundlerepository.Requirement freq : felixResource.getRequirements()) {
            Requirement req = m_resourCreator.createRequirement(freq.getName());
            
            req.setComment(freq.getComment());
            req.setFilter(freq.getFilter());
            req.setExtend(freq.isExtend());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
            out.addRequirement(req);
        }
        for (String fcat : felixResource.getCategories()) {
            out.addCategory(fcat);
        }

        Map<String, Object> felixProperties = felixResource.getProperties();
        
        for (String key : felixProperties.keySet()) {
            Object value = felixProperties.get(key);
            if (value instanceof String) {
                out.setProperty(key, (String) value);
            } else if (value instanceof Double) {
                out.setProperty(key, (Double) value);
            } else if (value instanceof Long) {
                out.setProperty(key, (Long) value);
            } else if (value instanceof URI) {
                out.setProperty(key, (URI) value);
            } else if (value instanceof Version) {
                out.setProperty(key, (Version) value);
            } else if (value instanceof List) {
                Set set = new HashSet();
                set.addAll((List) value);
                out.setProperty(key, set);
            } else {
                out.setProperty(key, value.toString());
            }
        }
        
        out.setSymbolicName(felixResource.getSymbolicName());
        out.setPresentationName(felixResource.getPresentationName());
        out.setSize(felixResource.getSize() != null ? felixResource.getSize() : 0);
        try {
            out.setUri(new URI(felixResource.getURI()));
        } catch (Exception ex) {
//            System.out.println("Exception: " + ex.getLocalizedMessage() + ", uri: " + resource.getURI());
//            setUri(null); // TODO co s tim?
        }
        
        out.setVersion(felixResource.getVersion());
        
        return out;
        
//        return new ConvertedResource(parseMetadata(parser));
    }

    public ResourceImpl parseMetadata(XmlPullParser reader) throws Exception {
        PullParser parser = new PullParser();
        int event;
        ResourceImpl resource = null;

        while ((event = reader.nextTag()) == XmlPullParser.START_TAG) {
            String element = reader.getName();

            if (RepositoryParser.RESOURCE.equals(element)) {
                resource = parser.parseResource(reader);
                event = reader.nextTag();
                break;
            } else if (resource == null) {
                resource = new ResourceImpl();
            }

            if (RepositoryParser.CATEGORY.equals(element)) {
                String category = parser.parseCategory(reader);
                resource.addCategory(category);
            } else if (RepositoryParser.CAPABILITY.equals(element)) {
                CapabilityImpl capability = parser.parseCapability(reader);
                resource.addCapability(capability);
            } else if (RepositoryParser.REQUIRE.equals(element)) {
                RequirementImpl requirement = parser.parseRequire(reader);
                resource.addRequire(requirement);
            } else {
                StringBuffer sb = null;
                String type = reader.getAttributeValue(null, "type");
                while ((event = reader.next()) != XmlPullParser.END_TAG) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            throw new Exception("Unexpected element inside <require/> element");
                        case XmlPullParser.TEXT:
                            if (sb == null) {
                                sb = new StringBuffer();
                            }
                            sb.append(reader.getText());
                            break;
                    }
                }
                if (sb != null) {
                    resource.put(element, sb.toString().trim(), type);
                }
            }

        }

        if (event != XmlPullParser.END_TAG || !OBR.equals(reader.getName())) {
            throw new Exception("Unexpected state");
        }

        return resource;

    }

    public void writeResource(Resource resource, Writer writer) throws IOException {
        XmlWriter w = new XmlWriter(writer);
        toXml(w, resource, false);
    }

    private static void toXml(XmlWriter w, Resource resource, boolean metafile) throws IOException {
        if (resource == null || w == null) {
            return;
        }
        String version = resource.getVersion().toString();
        if (metafile && "0.0.0".equals(version)) {
            version = null;
        }
        w.element(RepositoryParser.RESOURCE)
                .attribute(ID, resource.getId())
                .attribute(SYMBOLIC_NAME, resource.getSymbolicName())
                .attribute(PRESENTATION_NAME, resource.getPresentationName())
                .attribute(URI, metafile ? null : getRelativeUri(resource, URI))
                .attribute(VERSION, version);

        for (Property property : resource.getProperties()) {
            String name = property.getName();
            String value = property.getValue();
            Type type = property.getType();
            if (!name.equals(ID) &
                    !name.equals(SYMBOLIC_NAME) &
                    !name.equals(PRESENTATION_NAME) &
                    !name.equals(URI) &
                    !name.equals(VERSION)) {
                    w.element(name)
                            .attribute("type", type != null ? type : "null")
                            .text(value != null ? value : "null")
                            .end();
                    
                    
            }
        }
            
        String[] categories = resource.getCategories();
        for (int i = 0; categories != null && i < categories.length; i++) {
            w.element(RepositoryParser.CATEGORY).attribute(RepositoryParser.ID, categories[i])
                    .end();
        }
        for (Capability capability : resource.getCapabilities()) {
            toXml(w, capability);
        }
        for (Requirement requirement : resource.getRequirements()) {
            toXml(w, requirement);
        }
        w.end();
    }

    private static String getRelativeUri(Resource resource, String name) {
        String uri = resource.getPropertiesMap().get(name);
        if (resource instanceof ResourceImpl) {
            try {
                uri = java.net.URI.create(((ResourceImpl) resource).getRepository().getURI())
                        .relativize(java.net.URI.create(uri))
                        .toASCIIString();
                
            } catch (Throwable t) {
            }
        }
        return uri;
    }

    private static void toXml(XmlWriter w, Capability capability) throws IOException {
        if (capability == null || w == null) {
            return;
        }
        w.element(RepositoryParser.CAPABILITY).attribute(RepositoryParser.NAME, capability.getName());
        Property[] props = capability.getProperties();
        for (int j = 0; props != null && j < props.length; j++) {
            toXml(w, props[j]);
        }
        w.end();
    }

    private static void toXml(XmlWriter w, Property property) throws IOException {
        if (property == null || w == null) {
            return;
        }
        w.element(RepositoryParser.P)
                .attribute(RepositoryParser.N, property.getName())
                .attribute(RepositoryParser.T, property.getType() == Type.STRING ? null : property.getType())
                .attribute(RepositoryParser.V, property.getValue())
                .end();
    }

    private static void toXml(XmlWriter w, Requirement requirement) throws IOException {
        if (requirement == null || w == null) {
            return;
        }
        w.element(RepositoryParser.REQUIRE)
                    .attribute(RepositoryParser.NAME, requirement.getName())
                    .attribute(RepositoryParser.FILTER, requirement.getFilter())
                    .attribute(RepositoryParser.EXTEND, Boolean.toString(requirement.isExtend()))
                    .attribute(RepositoryParser.MULTIPLE, Boolean.toString(requirement.isMultiple()))
                    .attribute(RepositoryParser.OPTIONAL, Boolean.toString(requirement.isOptional()));
        if (requirement.getComment() != null) {
            w.text(requirement.getComment().trim());
        } 
        w.end();
    }
    
    private static void toXml(XmlWriter w, Repository repository) throws IOException {
        if (repository == null || w == null) {
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss.SSS");
        w.element(RepositoryParser.REPOSITORY)
                .attribute(RepositoryParser.NAME, repository.getName())
                .attribute(RepositoryParser.LASTMODIFIED, format.format(new Date(repository.getLastModified())));

        // TODO referrals
        
//        if (repository instanceof RepositoryImpl)
//        {
//            Referral[] referrals = ((RepositoryImpl) repository).getReferrals();
//            for (int i = 0; referrals != null && i < referrals.length; i++)
//            {
//                w.element(RepositoryParser.REFERRAL)
//                    .attribute(RepositoryParser.DEPTH, new Integer(referrals[i].getDepth()))
//                    .attribute(RepositoryParser.URL, referrals[i].getUrl())
//                    .end();
//            }
//        }

        Resource[] resources = repository.getResources();
        for (int i = 0; resources != null && i < resources.length; i++) {
            toXml(w, resources[i], false);
        }

        w.end();
    }

}