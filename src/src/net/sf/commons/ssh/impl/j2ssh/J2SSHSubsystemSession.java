package net.sf.commons.ssh.impl.j2ssh;

import com.sshtools.j2ssh.SshClient;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;

import java.io.IOException;

/**
 * @author veentoo
 * @date 5/5/2016
 */
public class J2SSHSubsystemSession extends J2SSHShellSession implements SubsystemSession {
    /**
     * @param properties
     * @param connection
     */
    public J2SSHSubsystemSession(Properties properties, SshClient connection)
    {
        super(properties, connection);
    }

    @Override
    protected void openImpl() throws IOException
    {
        log.trace("openImpl(): open j2ssh shell session");
        SubsystemSessionPropertiesBuilder sspb = SubsystemSessionPropertiesBuilder.getInstance();
        sspb.verify(this);
        try
        {
            boolean isSuccess = session.requestPseudoTerminal(sspb.getTerminalType(this),
                    sspb.getTerminalCols(this),
                    sspb.getTerminalRows(this),
                    sspb.getTerminalWidth(this),
                    sspb.getTerminalHeight(this),
                    "");
            if(!isSuccess)
                throw new IOException("Can't open pseudo terminal");
            if(!session.startSubsystem(sspb.getSubsystemName(this)))
                throw new IOException("Can't start shell");
        }
        catch (Exception e)
        {
            try
            {
                session.close();
            }
            catch (Exception e1)
            {
                log.error("can't close session",e);
            }
            if(e instanceof RuntimeException)
                throw (RuntimeException) e;
            if(e instanceof IOException)
                throw (IOException)e;
            throw new UnexpectedRuntimeException(e.getMessage(),e);
        }
    }
}
