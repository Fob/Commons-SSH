package net.sf.commons.ssh.event;

public interface EventEngine
{
    void throwEvent(Event event);
    void notifyFirst(EventEngine parentEngine);
    void notifyLast(EventEngine parentEngine);
    void associateWithProcessor(EventProcessor processor);
}
