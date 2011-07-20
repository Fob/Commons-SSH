package net.sf.commons.ssh.options;


public interface Properties {
    Object getProperty(String key);
    void updateFrom(Properties properties);
    void includeDefault(Properties configurable);
}
