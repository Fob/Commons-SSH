package net.sf.commons.ssh.options;



public class SystemConfig implements Properties //todo from abstract
{

    public Object getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public String toString() {
        return System.getProperties().entrySet()+"\n";
    }

}
