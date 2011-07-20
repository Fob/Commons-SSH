package net.sf.commons.ssh.utils;

public interface Closable extends java.io.Closeable
{
    boolean isClosed();
    boolean isClosing();
}
