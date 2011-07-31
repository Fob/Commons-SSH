/**
 * 
 */
package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 30.07.2011
 * @since 2.0
 */
public class ProducerEventFilter extends AbstractEventFilter
{
	
	private Object producer;

	public ProducerEventFilter(Object producer)
	{
		super();
		this.producer = producer;
	}

	@Override
	protected boolean checkEvent(Event event)
	{
		return producer.equals(event.getProducer());
	}
		
}
