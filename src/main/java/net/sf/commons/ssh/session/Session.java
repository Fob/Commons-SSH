package net.sf.commons.ssh.session;

import java.io.IOException;

import net.sf.commons.ssh.common.Container;

public interface Session extends Container
{
    void open() throws IOException;
    boolean isOpened();
    boolean isOpening();
}
