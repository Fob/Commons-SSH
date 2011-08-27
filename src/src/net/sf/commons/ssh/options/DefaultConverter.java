/**
 * 
 */
package net.sf.commons.ssh.options;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fob
 * @date 11.08.2011
 * @since 2.0
 */
public class DefaultConverter implements TypeConverter
{
	protected final Log log = LogFactory.getLog(this.getClass());
	protected Class<? extends PropertiesBuilder> builderCls;
	protected Map<String, PropertyType> keyMapping;
	protected Map<Class, Map<Class, Method>> convertMethods;

	/**
	 * @param builderCls
	 */
	public DefaultConverter(Class<? extends PropertiesBuilder> builderCls)
	{
		this.builderCls = builderCls;
		LogUtils.trace(log, "create converter for builder {0}", builderCls);
		keyMapping = new HashMap<String, PropertyType>();
		Field[] fields = builderCls.getDeclaredFields();
		for (Field field : fields)
		{
			PropertyType propertyType = field.getAnnotation(PropertyType.class);
			if (propertyType != null)
			{
				LogUtils.trace(log, "found anotation {0} for {1}", propertyType,field.getName());
				try
				{
					keyMapping.put((String) field.get(builderCls), propertyType);
				}
				catch (IllegalAccessException e)
				{
					throw new IllegalArgumentException(e);
				}
			}
		}
		LogUtils.trace(log, "found mapping:\n{0}", keyMapping);
		
		convertMethods = new HashMap<Class, Map<Class,Method>>();
		Method[] methods = this.getClass().getDeclaredMethods();
		for(Method method: methods)
		{
			ConvertMethod convertMetod = method.getAnnotation(ConvertMethod.class);
			if(convertMetod == null)
				continue;
			if(method.getParameterTypes().length > 1)
				continue;
			LogUtils.trace(log, "found converter methods {0}:{1}", method.getName(),convertMetod);
			Map<Class,Method> toMethods = convertMethods.get(convertMetod.to());
			if(toMethods == null)
				toMethods = new HashMap<Class, Method>();
			toMethods.put(convertMetod.from(),method);
			convertMethods.put(convertMetod.to(), toMethods);			
		}
		
		LogUtils.trace(log, "collect convert methods:\n{0}", convertMethods);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.options.TypeConverter#convert(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public Object convert(Object value, String key)
	{
		LogUtils.trace(log, "convert value = {0} key = {1}", value,key);
		if(value == null)
			return null;
		PropertyType propertyType = keyMapping.get(key);
		if(propertyType == null)
		{
			LogUtils.info(log, "property {0}.{1} hasn''t property type", builderCls.getName(),key);
			return value;
		}
		Class valueCls = value.getClass();
		if(propertyType.value().isAssignableFrom(valueCls))
			return value;
		
		
		Map<Class,Method> toMethods  = convertMethods.get(propertyType.value());
		if(toMethods == null)
		{
			LogUtils.trace(log, "can''t convert to {0}", propertyType.value());
		}
		Method convertMethod = null;
		if (toMethods != null)
		{
			convertMethod = toMethods.get(valueCls);
			if (convertMethod == null)
			{
				Class from = Object.class;
				for (Class cls : toMethods.keySet())
					if (cls.isAssignableFrom(valueCls) && from.isAssignableFrom(cls))
						from = cls;
				convertMethod = toMethods.get(from);
			}
		}
		if(convertMethod == null)
		{
			Object result = staticConvert(valueCls, propertyType.value(), value);
			if(result!=null)
				return result;
			LogUtils.error(log, "can''t found converter to convert property ''{0}''=''{3}'' from class {1} to class {2}", key,valueCls,propertyType.value(),value);
			throw new IllegalArgumentException("can't found converter to convert property '"+key+"'='"+value+"' from class "+valueCls.getName()
					+" to class "+propertyType.value().getName());
		}
		
		try
		{
			LogUtils.trace(log, "convert using: {0}", convertMethod);
			log.trace(this.getClass());
			return convertMethod.invoke(this, value);
		}
		catch (Exception e)
		{
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new UnexpectedRuntimeException(e);
		}
	}

	@ConvertMethod(from = Object.class, to = String.class)
	public String convertObjectToString(Object value)
	{
		return value.toString();
	}

	protected Object staticConvert(Class from,Class to,Object value)
	{
		Object result = null;
		try
		{
			LogUtils.trace(log, "try to get constructor from class {0}", from);
			Constructor constructor = to.getConstructor(from);
			result = constructor.newInstance(value);
			return result;
		}
		catch (Exception e)
		{
			LogUtils.trace(log,e, "constructor not found");
		}
		
		try
		{
			LogUtils.trace(log, "try to get valueOf from class {0}", from);
			Method method = to.getMethod("valueOf",from);
			result = method.invoke(to, from);
			return result;
		}
		catch (Exception e)
		{
			LogUtils.trace(log,e, "valueOf not found");
		}
		return null;
	}

}
