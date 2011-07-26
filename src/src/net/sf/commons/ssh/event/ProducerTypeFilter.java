package net.sf.commons.ssh.event;


public class ProducerTypeFilter extends AbstractEventFilter
{
    protected ProducerType producerType;

    public ProducerTypeFilter(ProducerType producerType, EventFilter andFilter)
    {
        super(andFilter);
        this.producerType = producerType;
    }

    public ProducerTypeFilter(ProducerType producerType)
    {
        this(producerType,null);
    }

    @Override
    public boolean check(Event event)
    {
        return producerType == event.getProducerType() && super.check(event);
    }
}
