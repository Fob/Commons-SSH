package net.sf.commons.ssh.options;

public abstract class AbstractConfigurable extends AbstractProperties implements Configurable
{

	

	public void updateFrom(Properties properties)  throws CloneNotSupportedException
	{
		Properties config = this.clone();
		properties.includeDefault(config);
		parent = properties;
		cleanSelfConfig();
	}

	
	@Override
	protected Properties clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException("Clone() should be overriden in children to work updateFrom");
	}

	/**
	 * Clean self properties container.
	 * Note: don't clean parent Properties 
	 */
	protected abstract void cleanSelfConfig();

}
