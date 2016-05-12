package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import org.apache.sshd.client.channel.ChannelSession;

/**
 * @author veentoo
 * @date 5/5/2016
 */
public class SSHDSubsystemSync extends SSHDShellSync implements SubsystemSession {

    public SSHDSubsystemSync(Properties properties, ChannelSession channel)
    {
        super(properties, channel);
    }
}
