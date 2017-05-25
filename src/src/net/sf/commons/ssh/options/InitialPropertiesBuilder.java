/**
 * 
 */
package net.sf.commons.ssh.options;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.newSetFromMap;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class InitialPropertiesBuilder extends PropertiesBuilder
{
	private static InitialPropertiesBuilder instance = null;

	@PropertyType(Boolean.class)
	public static final String SYNCHRONIZED_CONFIGURABLE = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedConfigurable";
	@PropertyType(Boolean.class)
	public static final String SYNCHRONIZED_ERROR_HOLDER = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedErrorHolder";
	@PropertyType(value = Boolean.class, required = true)
	public static final String SYNCHRONIZED_CHILDREN = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedChildren";
	@PropertyType(value = Set.class, required = true)
	public static final String LIBRARY_OPTIONS = "net.sf.commons.ssh.options.InitialPropertiesBuilder.options";

	@SuppressWarnings("unchecked")
	protected InitialPropertiesBuilder()
	{
		defaultProperties.put(SYNCHRONIZED_CONFIGURABLE, true);
		defaultProperties.put(SYNCHRONIZED_ERROR_HOLDER, true);
		defaultProperties.put(SYNCHRONIZED_CHILDREN, true);
		defaultProperties.put(LIBRARY_OPTIONS, newSetFromMap(new ConcurrentHashMap()));
	}

	public synchronized static InitialPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new InitialPropertiesBuilder();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getLibraryOptions(Properties config)
	{
		return (Set<String>) getProperty(config, LIBRARY_OPTIONS);
	}

	public void addLibraryOption(Configurable config, String value)
	{
		Set<String> options = getLibraryOptions(config);
		if (options == null)
		{
			options = new HashSet<String>();
			setProperty(config, LIBRARY_OPTIONS, options);
		}
		if (!options.contains(value))
			options.add(value);
	}

	public void addLibraryOption(Configurable config, HashSet<String> value)
	{

		setProperty(config, LIBRARY_OPTIONS, value);
	}
}
