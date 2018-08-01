package net.sf.commons.ssh.connector;

import net.sf.commons.ssh.Feature;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedFeatures
{
    public Feature[] value();
}
