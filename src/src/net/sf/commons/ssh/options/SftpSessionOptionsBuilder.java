package net.sf.commons.ssh.options;


public class SftpSessionOptionsBuilder extends OptionsBuilder
{
    public static final String KEY_DEFAULT_PERMISSION="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.defaultPermissions";
    public static final String KEY_LOCAL_DIRECTORY="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.localCurrentDirectory";
    public static final String KEY_REMOTE_DIRECTORY="net.sf.commons.ssh.options.SftpSessionOptionsBuilder.remoteCurrentDirectory";


    public SftpSessionOptionsBuilder(Options options)
    {
        super(options);
    }

    public SftpSessionOptionsBuilder()
    {
    }

    @Override
    protected void initDefault()
    {
        setDefaultPermissions(0022);
    }

    public int getDefaultPermissions()
    {
        return (Integer)options.getProperty(KEY_DEFAULT_PERMISSION);
    }

    public void setDefaultPermissions(int defaultPermissions)
    {
        options.setProperty(KEY_DEFAULT_PERMISSION,defaultPermissions);
    }

    public String getLocalCurrentDirectory()
    {
        return (String) options.getProperty(KEY_LOCAL_DIRECTORY);
    }

    public void setLocalCurrentDirectory(String localCurrentDirectory)
    {
        options.setProperty(KEY_LOCAL_DIRECTORY,localCurrentDirectory);
    }

    public String getRemoteCurrentDirectory()
    {
        return (String) options.getProperty(KEY_REMOTE_DIRECTORY);
    }

    public void setRemoteCurrentDirectory(String remoteCurrentDirectory)
    {
        options.setProperty(KEY_REMOTE_DIRECTORY,remoteCurrentDirectory);
    }

    //static

    public static void initDefault(Options options)
    {
        setDefaultPermissions(options,0022);
    }

    public static int getDefaultPermissions(Options options)
    {
        return (Integer)options.getProperty(KEY_DEFAULT_PERMISSION);
    }

    public static void setDefaultPermissions(Options options,int defaultPermissions)
    {
        options.setProperty(KEY_DEFAULT_PERMISSION,defaultPermissions);
    }

    public static String getLocalCurrentDirectory(Options options)
    {
        return (String) options.getProperty(KEY_LOCAL_DIRECTORY);
    }

    public static void setLocalCurrentDirectory(Options options,String localCurrentDirectory)
    {
        options.setProperty(KEY_LOCAL_DIRECTORY,localCurrentDirectory);
    }

    public static String getRemoteCurrentDirectory(Options options)
    {
        return (String) options.getProperty(KEY_REMOTE_DIRECTORY);
    }

    public static void setRemoteCurrentDirectory(Options options,String remoteCurrentDirectory)
    {
        options.setProperty(KEY_REMOTE_DIRECTORY,remoteCurrentDirectory);
    }

}
