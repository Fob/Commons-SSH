package net.sf.commons.ssh.options;

import net.sf.commons.ssh.options.impl.PropertiesWrapper;

/**
 * Base implementation of {@link Configurable}
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
public abstract class AbstractConfigurable extends AbstractProperties implements Configurable {
    /**
     * update properties of this config by another
     *
     * @param properties source readonly configuration
     * @throws CloneNotSupportedException if method {@link Object#clone()} doesn't overridden
     */
    public void updateFrom(Properties properties) throws CloneNotSupportedException {
        PropertiesWrapper updateFrom = new PropertiesWrapper(properties);

        updateFrom.parent = this.clone();
        parent = updateFrom;
        cleanSelfConfig();
    }

    /**
     * Clone configuration. New Configuration has the same set od properties and the same parent.
     *
     * @return ReadOnly copy of configuration
     * @throws CloneNotSupportedException if method {@link Object#clone()} doesn't overridden
     */
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public final Properties clone() throws CloneNotSupportedException {
        AbstractProperties result = cloneSelfProperties();
        result.parent = parent;
        return result;
    }

    /**
     * Clone self property container.
     *
     * @return cloned configuration
     */
    protected abstract AbstractProperties cloneSelfProperties();

    /**
     * Clean self properties container.
     * Note: don't clean parent Properties
     */
    protected abstract void cleanSelfConfig();

}
