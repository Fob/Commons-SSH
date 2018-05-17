package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;


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
    @PropertyType(ProxyType.class)
    public static final String KEY_PROXY_TYPE = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxyType";
    @PropertyType(String.class)
    public static final String KEY_PROXY_HOST = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxyHost";
    @PropertyType(Integer.class)
    public static final String KEY_PROXY_PORT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxyPort";
    @PropertyType(String.class)
    public static final String KEY_PROXY_USER = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxyUser";
    @PropertyType(String.class)
    public static final String KEY_PROXY_PASSWD = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.proxyPasswd";
    @PropertyType(value = String.class, required = true)
    public static final String KEY_HOST = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.host";
	@PropertyType(value = AuthenticationMethod.class, required = true)
	public static final String KEY_AUTHENTICATION_METHOD = "net.sf.commons.ssh.auth.authenticationMethod";
    @PropertyType(value = Boolean.class)
    public static final String KEY_NEED_AUTHENTICATION = "net.sf.commons.ssh.auth.needAuthentication";
    @PropertyType(Long.class)
    public static final String KEY_AUTHENTICATE_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.authTimeout";
    @PropertyType(Integer.class)
    public static final String SERVER_ALIVE_COUNT_MAX = "net.sf.commons.ssh.options.serverAliveCountMax";

    public ConnectionPropertiesBuilder()
    {
        defaultProperties.put(KEY_PORT, 22);
        defaultProperties.put(KEY_HOST,"127.0.0.1");
        defaultProperties.put(KEY_NEED_AUTHENTICATION, true);
        defaultProperties.put(KEY_AUTHENTICATION_METHOD, AuthenticationMethod.PASSWORD);
        defaultProperties.put(SERVER_ALIVE_COUNT_MAX, 0);
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
        setProperty(opt, KEY_CONNECT_TIMEOUT, value);
    }

    public String getHost(Properties opt)
    {
        return (String) getProperty(opt, KEY_HOST);
    }

    public void setHost(Configurable opt, String value)
    {
        setProperty(opt,KEY_HOST, value);
    }

    public ProxyType getProxyType(Properties opt)
    {
        return (ProxyType) getProperty(opt, KEY_PROXY_TYPE);
    }

    public void setProxyType(Configurable opt, ProxyType value)
    {
        setProperty(opt, KEY_PROXY_TYPE, value);
    }

    public String getProxyHost(Properties opt) {
        return (String)getProperty(opt, KEY_PROXY_HOST);
    }

    public void setProxyHost(Configurable opt, String value) {
        setProperty(opt, KEY_PROXY_HOST, value);
    }

    public Integer getProxyPort(Properties opt) {
        return (Integer)getProperty(opt, KEY_PROXY_PORT);
    }

    public void setProxyPort(Configurable opt, Integer value) {
        setProperty(opt, KEY_PROXY_PORT, value);
    }

    public String getProxyUser(Properties opt) {
        return (String)getProperty(opt, KEY_PROXY_USER);
    }

    public void setProxyUser(Configurable opt, String value) {
        setProperty(opt, KEY_PROXY_USER, value);
    }

    public String getProxyPasswd(Properties opt) {
        return (String)getProperty(opt, KEY_PROXY_PASSWD);
    }

    public void setProxyPasswd(Configurable opt, String value) {
        setProperty(opt, KEY_PROXY_PASSWD, value);
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
        setProperty(opt, SERVER_ALIVE_COUNT_MAX, value);
    }

    public void setNeedAuthentication(Configurable opt, Boolean needAuthentication) {
        setProperty(opt, KEY_NEED_AUTHENTICATION, needAuthentication);
    }

    public Boolean isNeedAuthentication(Properties opt) {
        Object result =  getProperty(opt, KEY_NEED_AUTHENTICATION);
        return result == null ? true: (Boolean) result;
    }

    public int getServerAliveCountMax(Properties config) {
        return (Integer) getProperty(config, SERVER_ALIVE_COUNT_MAX);
    }



    
}
