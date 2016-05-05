package net.sf.commons.ssh.impl.ganymed;

import ch.ethz.ssh2.Connection;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;

import java.io.IOException;

/**
 * @author veentoo
 * @date 5/5/2016
 */
public class GanymedSubsystemSession extends GanymedShellSession implements SubsystemSession {
    /**
     * @param properties
     * @param connection
     * @throws IOException
     */
    public GanymedSubsystemSession(Properties properties, Connection connection) throws IOException
    {
        super(properties, connection);
    }

    @Override
    protected void openImpl() throws IOException
    {
        SubsystemSessionPropertiesBuilder sspb = SubsystemSessionPropertiesBuilder.getInstance();
        sspb.verify(this);

        session.requestPTY(sspb.getTerminalType(this),
                sspb.getTerminalCols(this),
                sspb.getTerminalRows(this),
                sspb.getTerminalWidth(this),
                sspb.getTerminalHeight(this),
                null);
        session.startSubSystem(sspb.getSubsystemName(this));
        session.getStderr(); // for unlock
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }
}
