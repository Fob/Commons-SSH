package net.sf.commons.ssh.options;

import java.lang.reflect.Field;

public abstract class PropertiesBuilder
{
    private Configurable config;

    public static final String ASYNC="net.sf.commons.ssh.options.PropertiesBuilder.async";

    protected PropertiesBuilder(Configurable config)
    {
        this.config = config;
    }

    public void verify() throws IllegalPropertyException
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields)
        {
            Required annotation = field.getAnnotation(Required.class);
            if(annotation!=null)
            {
                if(!(field.getClass().equals(String.class)))
                {
                    throw new UnsupportedOperationException("Annotation Required can be aplied only to String field");
                }
                String key = field.toString();
                if(config.getProperty(key)==null)
                    throw new IllegalPropertyException(key,null);
            }
        }
    }
}
