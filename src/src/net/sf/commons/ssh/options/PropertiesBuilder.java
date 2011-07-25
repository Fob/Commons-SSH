package net.sf.commons.ssh.options;

import java.lang.reflect.Field;

public abstract class PropertiesBuilder
{
	

	protected PropertiesBuilder()
	{
		super();
	}

	public void verify(Properties config) throws IllegalPropertyException
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
