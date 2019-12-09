package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Property;

/**
 * Base class for comparing endpoint features - parameters, responses, ...
 *
 */
public abstract class EndpointFeatureComparator {

    /**
     * Compares ATTR__RESTIMPL_DATETYPE of two properties.
     *
     * This may be a bit tricky because e.g. long <: short and long <: int
     * but in java Long, Short, Integer are subclasses of Number and can't be
     * compared between each other (not assignable, not instance of).
     *
     * For unknown types (those outside java.lang) UNK is returned.
     *
     * @param p1 First property.
     * @param p2 Second property.
     * @return
     */
    protected Difference compareDateTypeAttributes(Property p1, Property p2) {
        Attribute dt1 = p1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);
        Attribute dt2 = p2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);

        String c1Name = dt1 != null ? dt1.getStringValue() : null;
        String c2Name = dt2 != null ? dt2.getStringValue() : null;

        if (c1Name == null || c1Name.isEmpty() || c2Name == null || c2Name.isEmpty()) {
            return Difference.UNK;
        }

        if (c1Name.equals(c2Name)) {
            return Difference.NON;
        }

        if (!c1Name.startsWith("java.lang") || !c2Name.startsWith("java.lang")) {
            return Difference.UNK;
        }

        try {
            Class<?> c1 = Class.forName(c1Name);
            Class<?> c2 = Class.forName(c2Name);

            if (c1.isAssignableFrom(c2)) {
                // c2 <: c1
                return Difference.SPE;
            } else if (c2.isAssignableFrom(c1)) {
                // c1 <: c2
                return Difference.INS;
            }
        } catch (ClassNotFoundException e) {
            // since this method only works with "java.lang", this
            // exception should not be thrown
            // todo: log exception
            e.printStackTrace();
        }

        return Difference.UNK;
    }
}
