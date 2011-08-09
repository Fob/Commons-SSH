package net.sf.commons.ssh.common;

public interface Closable extends java.io.Closeable
{
    boolean isClosed();
    boolean isClosedWithChildren();
    boolean isClosing();
    void clearClosed();
}
