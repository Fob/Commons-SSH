package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.options.PropertiesBuilder;



public class ConnectionPropertiesBuilder extends PropertiesBuilder
{
    private static ConnectionPropertiesBuilder instance = null;

    public ConnectionPropertiesBuilder()
    {
    }

    public synchronized static ConnectionPropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new ConnectionPropertiesBuilder();
        }
        return instance;
    }

    /*   *//**
 * The timeout value for the key exchange
 * <p/>
 * When this time limit is reached the transport protocol will initiate a
 * key re-exchange. The default value is one hour with the minimum timeout
 * being 60 seconds.
 *
 * @since 1.0
 *//*

    public static final String KEY_KEX_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.kexTimeout";
    *//**
 * The port to connect to on the remote host
 *
 * @since 1.0
 *//*

    public static final String KEY_PORT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.port";
    *//**
 * The send ignore flag to send random data packets
 * <p/>
 * If this flag is set to true, then the transport protocol will send
 * additional SSH_MSG_IGNORE packets with random data.
 *
 * @since 1.0
 *//*

    public static final String KEY_SEND_IGNORE = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.sendIgnore";
    *//**
 * Enable/disable {@link java.net.SocketOptions#SO_TIMEOUT} with the specified
 * timeout, in milliseconds. With this option set to a non-zero timeout, a
 * {@link java.io.InputStream#read()} will block for only this amount of time.
 * <p/>
 * If the timeout expires, a {@link java.net.SocketTimeoutException} is raised,
 * though the Socket is still valid.
 * <p/>
 * The timeout must be &gt; 0. A timeout of zero is interpreted as an
 * infinite timeout.
 *
 * @since 1.0
 *//*
    public static final String KEY_SOCKET_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.soTimeout";

    *//**
 * Enable/disable connectTimeout with a specified timeout value, in millisecond.
 * With this options set to a non-zero timeout, socket will connect to the server
 * for only this amount of time. A timeout of zero is interpreted as an infinite
 * timeout. The connection will then block until established or an error occurs.
 * <p/>
 * If the timeout expires, a {@link java.net.SocketTimeoutException} is raised.
 *
 * @since 1.4
 *//*
    public static final String KEY_CONNECT_TIMEOUT = "net.sf.commons.ssh.options.ConnectionOptionsBuilder.connectTimeout";


    public ConnectionPropertiesBuilder(Configurable options)
    {
        super(options);
    }

    *//**
 * Returns the timeout value for the key exchange
 *
 * @return the timeout value for the key exchange
 * @since 1.0
 *//*
    public int getKexTimeout()
    {
        return (Integer) options.getProperty(KEY_KEX_TIMEOUT);
    }

    *//**
 * @return the port to connect to on the remote host
 * @since 1.0
 *//*
    public int getPort()
    {
        return (Integer) options.getProperty(KEY_PORT);
    }

    *//**
 * @return the soTimeout
 * @since 1.0
 *//*
    public int getSoTimeout()
    {
        return (Integer) options.getProperty(KEY_SOCKET_TIMEOUT);
    }

    *//**
 * @return the connectTimeout
 * @since 1.4
 *//*
    public int getConnectTimeout()
    {
        return (Integer) options.getProperty(KEY_CONNECT_TIMEOUT);
    }

    *//**
 * @return the send ignore flag to send random data packets
 *//*
    public boolean isSendIgnore()
    {
        return (Boolean) options.getProperty(KEY_SEND_IGNORE);
    }

    *//**
 * @param kexTimeout the kexTimeout to set
 * @since 1.0
 *//*
    public void setKexTimeout(int kexTimeout)
    {
        options.setProperty(KEY_KEX_TIMEOUT, kexTimeout);
    }

    *//**
 * @param port the port to connect to on the remote host
 * @since 1.0
 *//*
    public void setPort(int port)
    {
        options.setProperty(KEY_PORT, port);
    }

    *//**
 * @param sendIgnore the send ignore flag to send random data packets
 * @since 1.0
 *//*
    public void setSendIgnore(boolean sendIgnore)
    {
        options.setProperty(KEY_SEND_IGNORE, sendIgnore);
    }

    *//**
 * Enable/disable {@link java.net.SocketOptions#SO_TIMEOUT} with the specified
 * timeout, in milliseconds. With this option set to a non-zero timeout, a
 * {@link java.io.InputStream#read()} will block for only this amount of time.
 * <p/>
 * If the timeout expires, a {@link java.net.SocketTimeoutException} is raised,
 * though the Socket is still valid.
 * <p/>
 * The timeout must be &gt; 0. A timeout of zero is interpreted as an
 * infinite timeout.
 *
 * @param soTimeout the soTimeout to set
 * @since 1.0
 *//*
    public void setSoTimeout(int soTimeout)
    {
        options.setProperty(KEY_SOCKET_TIMEOUT, soTimeout);
    }

    */

    /**
     * Enable/disable connectTimeout with a specified timeout value, in millisecond.
     * With this options set to a non-zero timeout, socket will connect to the server
     * for only this amount of time. A timeout of zero is interpreted as an infinite
     * timeout. The connection will then block until established or an error occurs.
     * <p/>
     * If the timeout expires, a {@link java.net.SocketTimeoutException} is raised.
     *
     * @param connectTimeout the connectTimeout to set
     * @since 1.4
     *//*
    public void setConnectTimeout(int connectTimeout)
    {
        options.setProperty(KEY_CONNECT_TIMEOUT, connectTimeout);
    }

    @Override
    protected void setupDefault()
    {
        setKexTimeout(3600);
        setPort(22);
        setSendIgnore(false);
        setSoTimeout(0);
        setConnectTimeout(0);
    }

    //static operators

    public static int getKexTimeout(Configurable options)
    {
        return (Integer) options.getProperty(KEY_KEX_TIMEOUT);
    }


    public static int getPort(Configurable options)
    {
        return (Integer) options.getProperty(KEY_PORT);
    }


    public static int getSoTimeout(Configurable options)
    {
        return (Integer) options.getProperty(KEY_SOCKET_TIMEOUT);
    }

    public static int getConnectTimeout(Configurable options)
    {
        return (Integer) options.getProperty(KEY_CONNECT_TIMEOUT);
    }

    public static boolean isSendIgnore(Configurable options)
    {
        return (Boolean) options.getProperty(KEY_SEND_IGNORE);
    }


    public static void setKexTimeout(Configurable options, int kexTimeout)
    {
        options.setProperty(KEY_KEX_TIMEOUT, kexTimeout);
    }


    public static void setPort(Configurable options, int port)
    {
        options.setProperty(KEY_PORT, port);
    }


    public static void setSendIgnore(Configurable options, boolean sendIgnore)
    {
        options.setProperty(KEY_SEND_IGNORE, sendIgnore);
    }


    public static void setSoTimeout(Configurable options, int soTimeout)
    {
        options.setProperty(KEY_SOCKET_TIMEOUT, soTimeout);
    }


    public static void setConnectTimeout(Configurable options, int connectTimeout)
    {
        options.setProperty(KEY_CONNECT_TIMEOUT, connectTimeout);
    }

    public static void setupDefault(Configurable options)
    {
        setKexTimeout(options, 3600);
        setPort(options, 22);
        setSendIgnore(options, false);
        setSoTimeout(options, 0);
        setConnectTimeout(options, 0);
    }*/
}
