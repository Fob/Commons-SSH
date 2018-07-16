package net.sf.commons.ssh.event;

public abstract class AbstractEvent implements Event
{
    protected  int severity = 0;
    protected int priority = 0;
    protected AbstractEventProcessor producer = null;
    protected EventType eventType;

    public AbstractEvent(AbstractEventProcessor producer)
    {
        this.producer = producer;
    }

    public int getSeverity()
    {
        return severity;
    }

    public void setSeverity(int severity)
    {
        this.severity = severity;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public EventProcessor getProducer()
    {
        return producer;
    }

    public void setProducer(AbstractEventProcessor producer)
    {
        this.producer = producer;
    }

    public ProducerType getProducerType()
    {
        return producer.getProducerType();
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }
}
