package net.sf.commons.ssh;




public enum Feature
{
    AUTH_CREDENTIALS,
    AUTH_PUBLICKEY,
    AUTH_NONE,
    SESSION_EXEC,
    SESSION_SFTP,
    SESSION_SHELL ,
    SESSION_SUBSYSTEM,
    SOCKET_TIMEOUT,
    CONNECTION_TIMEOUT ,
    AUTHENTICATE_TIMEOUT,
    ASYNCHRONOUS,
    SYNCHRONOUS,
    ERROR_STREAM,
    SSH1,
    SSH2;
}
