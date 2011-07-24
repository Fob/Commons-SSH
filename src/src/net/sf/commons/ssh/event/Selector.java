package net.sf.commons.ssh.event;

import net.sf.commons.ssh.common.Closable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Selector extends Closable
{
    void register(EventFilter filter);
    void unRegister(EventFilter filter);


    Collection<Event> select(long timeout,TimeUnit timeUnit) throws InterruptedException;
    Collection<Event> select() throws InterruptedException;

    Event waitFirst(long timeout,TimeUnit timeUnit) throws InterruptedException;
    Event waitFirst() throws InterruptedException;

    Collection<Event> waitAll(long timeout,TimeUnit timeUnit) throws InterruptedException;
    Collection<Event> waitAll() throws InterruptedException;
}
