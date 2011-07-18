package net.sf.commons.ssh.options;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Options
{
    protected Map<String,Object> properties = new HashMap<String,Object>();
    protected Options parentOptions;

    public Options(Options parentOptions)
    {
        this.parentOptions = parentOptions;
    }

    public Options()
    {
        this(null);
    }

    public Object getProperty(String key)
    {
        Object object = properties.get(key);
        if(object==null && parentOptions!=null)
            object = parentOptions.getProperty(key);
        return object;
    }

    public void setProperty(String key,Object object)
    {
        properties.put(key,object);
    }

    public Object getProperty(String key,Object def)
    {
        Object result = getProperty(key);
        return result==null?def:result;
    }

    public Options getParentOptions()
    {
        return parentOptions;
    }

    public void setParentOptions(Options parentOptions)
    {
        this.parentOptions = parentOptions;
    }

    protected Map<String, Object> getProperties()
    {
        return properties;
    }

    protected void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    public void updateOptions(Options options)
    {
        for(String key: options.getKeys())
        {
            if(!StringUtils.equals((String)options.getProperty(key), (String)getProperty(key)))
            {
                setProperty(key,options.getProperty(key));
            }
        }
    }

    public Set<String> getKeys()
    {
        Set<String> keys= new HashSet<String>();
        keys.addAll(properties.keySet());
        if(parentOptions!=null)
            keys.addAll(parentOptions.getKeys());
        return keys;
    }

    @Override
    public String toString()
    {
        return properties.toString()+"\n"+(parentOptions==null?"":parentOptions.toString());
    }
}
