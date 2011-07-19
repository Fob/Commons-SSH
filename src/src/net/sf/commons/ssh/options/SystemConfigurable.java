package net.sf.commons.ssh.options;



public class SystemConfigurable implements Properties
{

    public Object getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public String toString() {
        return System.getProperties().entrySet()+"\n";
    }
}
