package net.sf.commons.ssh.options;


import java.util.*;

public class AbstractConfigurable implements ConfigurableProperties
{
    protected Map<String,Object> properties = new HashMap<String,Object>();
    protected List<Properties> parentProperties = new ArrayList<Properties>();

    public Object getProperty(String key)
    {
        Object object = properties.get(key);
        if(object!=null)
            return object;
        for(Properties config:parentProperties)
        {
            object=config.getProperty(key);
            if(object!=null)
                break;
        }
        return object;
    }

    public void setProperty(String key,Object object)
    {
        properties.put(key,object);
    }

    public void updateFrom(Map<String, Object> properties) {
        for(Map.Entry<String,Object> en:properties.entrySet())
        {
            if(!en.getValue().equals(getProperty(en.getKey())))
                setProperty(en.getKey(),en.getValue());
        }
    }

    public void include(Properties configurable)
    {
        parentProperties.add(configurable);
    }

    @Override
    public String toString()
    {
        StringBuilder builder=new StringBuilder();
        builder.append(properties.toString()).append("\n");
        for(Properties config: parentProperties)
        {
            builder.append(config.toString());
        }
        return builder.toString();
    }
}
