package net.sf.commons.ssh.event;

public interface EventHandler<T>
{
    T handle(Event event);
}
