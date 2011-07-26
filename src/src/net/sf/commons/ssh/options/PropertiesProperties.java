package net.sf.commons.ssh.options;


import java.util.*;
import java.util.Properties;

public class PropertiesProperties extends AbstractProperties
{
    protected java.util.Properties config;

    public PropertiesProperties(Properties config)
    {
        this.config = (Properties) config.clone();
    }

    @Override
    protected Object getSelfProperty(String key)
    {
        return config.get(key);
    }
}
