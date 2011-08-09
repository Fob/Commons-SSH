package net.sf.commons.ssh.common;

import net.sf.commons.ssh.errors.ErrorHolder;
import net.sf.commons.ssh.event.EventProcessor;
import net.sf.commons.ssh.options.Configurable;

public interface Container extends Closable,Configurable,ErrorHolder,EventProcessor
{
	public Status getContainerStatus();
}
