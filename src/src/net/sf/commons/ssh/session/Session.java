package net.sf.commons.ssh.session;

import net.sf.commons.ssh.common.Container;

public interface Session extends Container
{
    void open();
    boolean isOpened();
    boolean isOpening();
}
