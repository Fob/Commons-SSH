package net.sf.commons.ssh;


/**
 * All Available Features
 */
public enum Feature {
    /**
     * GSS API authentication feature
     */
    AUTH_GSS_API,
    /**
     * Password authentication feature
     */
    AUTH_CREDENTIALS,
    /**
     * Public key authentication feature
     */
    AUTH_PUBLIC_KEY,
    /**
     * None Authentication feature
     */
    AUTH_NONE,

    /**
     * Shell session feature
     */
    SESSION_SHELL,

    /**
     * SCP session feature
     */
    SESSION_SCP,

    /**
     * Socket timeout supports
     */
    SOCKET_TIMEOUT,
    /**
     * Connection timeout supports
     */
    CONNECTION_TIMEOUT,
    /**
     * Authentication timeout supports
     */
    AUTHENTICATE_TIMEOUT,
    /**
     * Asynchronous mode
     */
    ASYNCHRONOUS,
    /**
     * Synchronous mode
     */
    SYNCHRONOUS,
    /**
     * Separate Std error stream
     */
    ERROR_STREAM,
    /**
     * SSH1 protocol supports
     */
    SSH1,
    /**
     * SSH2 protocol supports
     */
    SSH2,
    /**
     * Library provide api to connect to devices without authenticate
     */
    CONNECT_WITHOUT_AUTHENTICATE,
    /**
     * Support HTTP proxing
     */
    HTTP_PROXY,
    /**
     * Support SOCKS4 proxing
     */
    SOCKS4_PROXY,
    /**
     * Support SOCKS5 proxing
     */
    SOCKS5_PROXY

}
