package net.sf.commons.ssh.j2ssh;

import com.sshtools.j2ssh.ScpClient;
import net.sf.commons.ssh.ScpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class J2sshScpSession implements ScpSession{
    private final Log log = LogFactory.getLog(this.getClass());
    private final ScpClient scp;

    J2sshScpSession(final ScpClient scp) {
        log.trace("<init>");
        this.scp = scp;
    }
    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isClosed() throws IOException {
        return false;//todo implement
    }


    @Override
    public void put(String from, String to, boolean b) throws IOException {
        scp.put(from,to,false);
    }
}
