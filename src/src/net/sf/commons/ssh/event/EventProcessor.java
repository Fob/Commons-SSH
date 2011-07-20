package net.sf.commons.ssh.event;


public interface EventProcessor
{
    Selector createSelector();
    void addEventHandler(EventHandler handler,EventFilter filter);
    void removeEventHandler(EventHandler handler);
}
