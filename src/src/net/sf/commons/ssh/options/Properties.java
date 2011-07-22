package net.sf.commons.ssh.options;


public interface Properties {
    Object getProperty(String key);
    void includeDefault(Properties configurable);
}
