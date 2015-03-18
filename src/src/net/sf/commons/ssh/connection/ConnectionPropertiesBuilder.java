package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.options.*;

import java.net.Proxy;


public class ConnectionPropertiesBuilder extends PropertiesBuilder
{
    private static ConnectionPropertiesBuilder instance = null;
    @PropertyType(Long.class)
    public static final String KEY_KEX_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.kexTimeout";
    @PropertyType(value = Integer.class,required = true)
    public static final String KEY_PORT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.port";
    @PropertyType(Boolean.class)
    public static final String KEY_SEND_IGNORE = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.sendIgnore";
    @PropertyType(Long.class)
    public static final String KEY_SOCKET_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.soTimeout";
    @PropertyType(Long.class)
    public static final String KEY_CONNECT_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.connectTimeout";
    @PropertyType(Proxy.class)
    public static final String KEY_PROXY = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxy";
    @PropertyType(value = String.class, required = true)
    public static final String KEY_HOST = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.host";
	@PropertyType(value = AuthenticationMethod.class, required = true)
	public static final String KEY_AUTHENTICATION_METHOD = "net.sf.commons.ssh.auth.authenticationMethod";
    @PropertyType(Long.class)
    public static final String KEY_AUTHENTICATE_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.authTimeout";

    public ConnectionPropertiesBuilder()
    {
        defaultProperties.put(KEY_PORT, Integer.valueOf(22));
        defaultProperties.put(KEY_HOST,"127.0.0.1");
        defaultProperties.put(KEY_AUTHENTICATION_METHOD, AuthenticationMethod.PASSWORD);
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
        setProperty(opt,KEY_KEX_TIMEOUT, value);
    }

    public Integer getPort(Properties opt)
    {
        return (Integer) getProperty(opt, KEY_PORT);
    }

    public void setPort(Configurable opt, Integer value)
    {
        setProperty(opt,KEY_PORT, value);
    }

    public Boolean getSendIgnore(Properties opt)
    {
        return (Boolean) getProperty(opt, KEY_SEND_IGNORE);
    }

    public void setSendIgnore(Configurable opt, Boolean value)
    {
        setProperty(opt,KEY_SEND_IGNORE, value);
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
        setProperty(opt,KEY_CONNECT_TIMEOUT, value);
    }

    public String getHost(Properties opt)
    {
        return (String) getProperty(opt, KEY_HOST);
    }

    public void setHost(Configurable opt, String value)
    {
        setProperty(opt,KEY_HOST, value);
    }

    public Proxy getProxy(Properties opt)
    {
        return (Proxy) getProperty(opt, KEY_PROXY);
    }

    public void setProxy(Configurable opt, Proxy value)
    {
        setProperty(opt, KEY_PROXY, value);
    }

	public void setAuthenticationMethod(Configurable config,AuthenticationMethod value)
	{
		setProperty(config, KEY_AUTHENTICATION_METHOD, value);
	}
	
	public AuthenticationMethod getAuthenticationMethod(Properties config)
	{
		return (AuthenticationMethod) getProperty(config, KEY_AUTHENTICATION_METHOD);		
	}
	
    public Long getAuthenticateTimeout(Properties opt)
    {
        return (Long) getProperty(opt, KEY_AUTHENTICATE_TIMEOUT);
    }

    public void setAuthenticateTimeout(Configurable opt, Long value)
    {
        setProperty(opt,KEY_AUTHENTICATE_TIMEOUT, value);
    }
    
}
