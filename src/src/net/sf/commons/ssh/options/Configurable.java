package net.sf.commons.ssh.options;

public interface Configurable extends Properties
{
	void setProperty(String key, Object value);

	void updateFrom(Properties properties) throws CloneNotSupportedException;
}
