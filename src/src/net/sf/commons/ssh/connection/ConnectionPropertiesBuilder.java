package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.options.*;


public class ConnectionPropertiesBuilder extends PropertiesBuilder
{
    private static ConnectionPropertiesBuilder instance = null;
    @Required
    @PropertyType(Long.class)
    public static final String KEY_KEX_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.kexTimeout";
    @Required
    @PropertyType(Integer.class)
    public static final String KEY_PORT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.port";
    @Required
    @PropertyType(Boolean.class)
    public static final String KEY_SEND_IGNORE = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.sendIgnore";
    @Required
    @PropertyType(Long.class)
    public static final String KEY_SOCKET_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.soTimeout";
    @Required
    @PropertyType(Long.class)
    public static final String KEY_CONNECT_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.connectTimeout";
    @Required
    @PropertyType(String.class)
    public static final String KEY_HOST = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.host";
    @Required
    public static final String KEY_AUTHENTICATION_OPTIONS = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.auth";

    public ConnectionPropertiesBuilder()
    {
        defaultProperties.put(KEY_KEX_TIMEOUT, Long.valueOf(3600));
        defaultProperties.put(KEY_PORT, Integer.valueOf(22));
        defaultProperties.put(KEY_SEND_IGNORE, false);
        defaultProperties.put(KEY_SOCKET_TIMEOUT, Long.valueOf(0));
        defaultProperties.put(KEY_CONNECT_TIMEOUT, Long.valueOf(0));
        defaultProperties.put(KEY_HOST,"127.0.0.1");
    }

    public synchronized static ConnectionPropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new ConnectionPropertiesBuilder();
        }
        return instance;
    }

    public Long getKexTimeout(Properties opt)
    {
        return (Long) getProperty(opt, KEY_KEX_TIMEOUT);
    }

    public void setKexTimeout(Configurable opt, Long value)
    {
        opt.setProperty(KEY_KEX_TIMEOUT, value);
    }

    public Integer getPort(Properties opt)
    {
        return (Integer) getProperty(opt, KEY_PORT);
    }

    public void setPort(Configurable opt, Integer value)
    {
        opt.setProperty(KEY_PORT, value);
    }

    public Boolean getSendIgnore(Properties opt)
    {
        return (Boolean) getProperty(opt, KEY_SEND_IGNORE);
    }

    public void setSendIgnore(Configurable opt, Boolean value)
    {
        opt.setProperty(KEY_SEND_IGNORE, value);
    }

    public Long getSoTimeout(Properties opt)
    {
        return (Long) getProperty(opt, KEY_SOCKET_TIMEOUT);
    }

    public void setSoTimeout(Configurable opt, Long value)
    {
        opt.setProperty(KEY_SOCKET_TIMEOUT, value);
    }

    public Long getConnectTimeout(Properties opt)
    {
        return (Long) getProperty(opt, KEY_CONNECT_TIMEOUT);
    }

    public void setConnectTimeout(Configurable opt, Long value)
    {
        opt.setProperty(KEY_CONNECT_TIMEOUT, value);
    }

    public String getHost(Properties opt)
    {
        return (String) getProperty(opt, KEY_HOST);
    }

    public void setHost(Configurable opt, String value)
    {
        opt.setProperty(KEY_HOST, value);
    }
    
    public AuthenticationOptions getAuthenticationOptions(Properties opt)
    {
    	return (AuthenticationOptions) opt.getProperty(KEY_AUTHENTICATION_OPTIONS);    	
    }
    
    public void setAuthenticationOptions(Configurable opt,AuthenticationOptions auth)
    {
    	opt.setProperty(KEY_AUTHENTICATION_OPTIONS, auth);    	
    }
}
