package net.sf.commons.ssh.options;


import java.util.Map;

public interface ConfigurableProperties extends Properties{
    void setProperty(String key,Object value);
    void updateFrom(Map<String,Object> properties);
    void include(Properties configurable);
}
