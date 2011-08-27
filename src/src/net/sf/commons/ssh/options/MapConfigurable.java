package net.sf.commons.ssh.options;

import java.util.HashMap;
import java.util.Map;

public class MapConfigurable extends AbstractConfigurable
{
    protected Map<String,Object> config = new HashMap<String,Object>();

    @Override
    protected void cleanSelfConfig()
    {
        config.clear();
    }

    @Override
    protected Object getSelfProperty(String key)
    {
        return config.get(key);
    }

    public void setProperty(String key, Object value)
    {
        config.put(key,value);
    }

    @Override
    protected Properties clone() throws CloneNotSupportedException
    {
        return new MapProperties(config);
    }

	@Override
	public String toString()
	{
		return config+super.toString();
	}
    
    
}
