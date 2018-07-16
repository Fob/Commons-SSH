package net.sf.commons.ssh.event;


public class ProducerTypeFilter extends AbstractEventFilter
{
    protected ProducerType producerType;

    public ProducerTypeFilter(ProducerType producerType)
    {
        this.producerType = producerType;
    }

    @Override
    protected boolean checkEvent(Event event)
    {
        return producerType == event.getProducerType();
    }
}
