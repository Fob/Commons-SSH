package net.sf.commons.ssh.options;

import net.sf.commons.ssh.common.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class PropertiesBuilder
{
    private final Log log = LogFactory.getLog(this.getClass());

    protected Map<String,Object> defaultProperties = new HashMap<String,Object>();
    protected TypeConverter converter;

	protected PropertiesBuilder()
	{
		super();
		converter = new DefaultConverter();
	}

    public Properties getDefault()
    {
        return new MapProperties(defaultProperties);
    }

	public void verify(Properties config) throws IllegalPropertyException
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields)
        {
        	PropertyType annotation = field.getAnnotation(PropertyType.class);
            if(annotation!=null && annotation.required())
            {
                if(!(field.getClass().equals(String.class)))
                {
                    throw new UnsupportedOperationException("PropertyType Required can be aplied only to String field");
                }
                String key = field.toString();
                if(config.getProperty(key)==null)
                    throw new IllegalPropertyException(key,null);
            }
        }
    }

    public Object getProperty(Properties properties,String key)
    {
        return getConverter().convert(properties.getProperty(key), key);
    }

    public void setProperty(Configurable properties,String key,Object value)
    {
        properties.setProperty(key,getConverter().convert(value, key));
    }
    
    public TypeConverter getConverter()
    {
    	return converter;     	    	
    }
}
