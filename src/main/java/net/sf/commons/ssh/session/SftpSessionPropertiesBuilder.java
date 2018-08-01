package net.sf.commons.ssh.session;

import net.sf.commons.ssh.options.*;

public class SftpSessionPropertiesBuilder extends PropertiesBuilder
{
    private static SftpSessionPropertiesBuilder instance = null;
    @PropertyType(Integer.class)
    public static final String KEY_DEFAULT_PERMISSION="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.defaultPermissions";
    @PropertyType(String.class)
    public static final String KEY_LOCAL_DIRECTORY="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.localCurrentDirectory";
    @PropertyType(String.class)
    public static final String KEY_REMOTE_DIRECTORY="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.remoteCurrentDirectory";


    public synchronized static SftpSessionPropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new SftpSessionPropertiesBuilder();
        }
        return instance;
    }

    public SftpSessionPropertiesBuilder()
    {
        defaultProperties.put(KEY_DEFAULT_PERMISSION,Integer.valueOf(0022));
    }

    public int getDefaultPermissions(Properties opt)
    {
        return (Integer)getProperty(opt, KEY_DEFAULT_PERMISSION);
    }

    public void setDefaultPermissions(Configurable opt,int defaultPermissions)
    {
        opt.setProperty(KEY_DEFAULT_PERMISSION,defaultPermissions);
    }

    public String getLocalCurrentDirectory(Properties opt)
    {
        return (String) getProperty(opt, KEY_LOCAL_DIRECTORY);
    }

    public void setLocalCurrentDirectory(Configurable opt,String localCurrentDirectory)
    {
        opt.setProperty(KEY_LOCAL_DIRECTORY,localCurrentDirectory);
    }

    public String getRemoteCurrentDirectory(Properties opt)
    {
        return (String) getProperty(opt, KEY_REMOTE_DIRECTORY);
    }

    public void setRemoteCurrentDirectory(Configurable opt,String remoteCurrentDirectory)
    {
        opt.setProperty(KEY_REMOTE_DIRECTORY,remoteCurrentDirectory);
    }
}
