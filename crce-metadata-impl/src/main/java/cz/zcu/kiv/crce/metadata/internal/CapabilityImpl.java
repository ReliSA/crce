package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Type;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CapabilityImpl extends AbstractPropertyProvider<Capability> implements Capability, PropertyProvider<Capability> {

    private String m_name;

    public CapabilityImpl(String name) {
        setProperty(name, name, Type.LONG);
        m_name = name.intern();
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    Capability getThis() {
        return this;
    }
}
