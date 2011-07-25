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

	protected PropertiesBuilder()
	{
		super();
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

    protected Object convertPropertyFromString(String key,Object value)
    {
        if(value == null)
            return null;
        if(!value.getClass().equals(String.class))
            return value;
        LogUtils.trace(log,"process convert property {0} from String",key);
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields)
        {
            try
            {
                if(key.equals(field.get(this)))
                {
                    PropertyType propertyType = field.getAnnotation(PropertyType.class);
                    if (propertyType == null)
                        return value;

                    Class propertyClass = propertyType.value();
                    LogUtils.trace(log, "try to convert class {0} to class {1}", String.class, propertyClass);
                    if (propertyClass.equals(value.getClass()))
                        return value;
                    try
                    {
                        Method method = propertyClass.getMethod("valueOf", String.class);
                        return method.invoke(propertyClass, value);
                    }
                    catch (NoSuchMethodException e)
                    {
                        LogUtils.error(log, e, "class {0} havn't valueOf Method", value.getClass());
                        throw new IllegalArgumentException("class " + value.getClass() + " should implement valueOf to autoconvert");
                    }
                    catch (InvocationTargetException e)
                    {
                        log.error("error while converting " + key + " to " + propertyClass);
                        throw new IllegalArgumentException("error while converting " + key + " to " + propertyClass);
                    }
                }

            }
            catch (IllegalAccessException e)
            {
                LogUtils.trace(log,e ,"can't access field {0}",field);
            }
        }
        log.trace("converter not found");
        return value;
    }

    public Object getProperty(Properties properties,String key)
    {
        return convertPropertyFromString(key, properties.getProperty(key));
    }

    public void setProperty(Configurable properties,String key,Object value)
    {
        properties.setProperty(key,convertPropertyFromString(key,value));
    }
}
