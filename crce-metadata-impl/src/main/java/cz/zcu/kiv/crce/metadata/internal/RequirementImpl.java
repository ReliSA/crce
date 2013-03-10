package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MatchingAttribute;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.SimpleAttributeType;

/**
 * Implementation of Requirement interface.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class RequirementImpl extends AbstractEntityBase implements Requirement {

    private String namespace = null;
    private Resource resource = null;
    private Requirement parent = null;
    private List<Requirement> nestedRequirements = new ArrayList<>();

    public RequirementImpl(String namespace) {
        this.namespace = namespace.intern();
    }
    
    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Requirement getParent() {
        return parent;
    }

    @Override
    public boolean setParent(Requirement parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public synchronized boolean addNestedRequirement(Requirement requirement) {
        if (!nestedRequirements.contains(requirement)) {
            requirement.setParent(this);
            return nestedRequirements.add(requirement);
        }
        return false;
    }

    @Override
    public synchronized boolean removeNestedRequirement(Requirement requirement) {
        return nestedRequirements.remove(requirement);
    }

    @Override
    public synchronized List<Requirement> getNestedRequirements() {
        return Collections.unmodifiableList(nestedRequirements);
    }

    @Override
    public synchronized <T> MatchingAttribute<T> getAttribute(AttributeType<T> type) {
        return (MatchingAttribute<T>) super.getAttribute(type);
    }

    @Override
    public synchronized <T> boolean setAttribute(AttributeType<T> type, T value, Operator operator) {
        return super.setAttribute(new MatchingAttributeImpl<>(type, value, operator));
    }

    @Override
    public synchronized <T> boolean setAttribute(Attribute<T> attribute, Operator operator) {
        return super.setAttribute(new MatchingAttributeImpl<>(attribute.getAttributeType(), attribute.getValue(), operator));
    }

    @Override
    public synchronized <T> boolean setAttribute(String name, Class<T> type, T value, Operator operator) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        Attribute<T> attribute = new MatchingAttributeImpl<>(attributeType, value, operator);
        return super.setAttribute(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<MatchingAttribute<?>> getAttributes() {
        return Collections.unmodifiableList((List<MatchingAttribute<?>>) super.getAttributes());
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Map<String, MatchingAttribute<?>> getAttributesMap() {
        return (Map<String, MatchingAttribute<?>>) super.getAttributesMap();
    }

    @Override
    public synchronized Operator getAttributeOperator(AttributeType<?> type) {
        return ((MatchingAttribute<?>) super.getAttribute(type)).getOperator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequirementImpl other = (RequirementImpl) obj;
        if (!Objects.equals(this.namespace, other.namespace)) {
            return false;
        }
        if (!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.nestedRequirements, other.nestedRequirements)) {
            return false;
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.namespace);
        hash = 97 * hash + Objects.hashCode(this.resource);
        hash = 97 * hash + Objects.hashCode(this.parent);
        hash = 97 * hash + Objects.hashCode(this.nestedRequirements);
        return 97 * hash + super.hashCode();
    }
}
