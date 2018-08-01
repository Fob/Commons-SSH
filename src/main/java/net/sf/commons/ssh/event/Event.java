package net.sf.commons.ssh.event;


public interface Event
{
    int getSeverity();

	int getPriority();

    EventProcessor getProducer();

    ProducerType getProducerType();

    EventType getEventType();

}
