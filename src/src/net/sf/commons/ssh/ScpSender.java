package net.sf.commons.ssh;

import net.sf.commons.ssh.Connection;
import net.sf.commons.ssh.PasswordAuthenticationOptions;
import net.sf.commons.ssh.ScpSession;
import net.sf.commons.ssh.j2ssh.J2sshConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScpSender {
    private static final Log log = LogFactory.getLog(ScpSender.class);

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int KEY_EXCHANGE_TIMEOUT_MS = 5000;
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static final int DEFAULT_PORT = 22;

    public static Connection connect( String host, String login, String pass) throws Exception{
        PasswordAuthenticationOptions authOptions = new PasswordAuthenticationOptions(login, pass);
        if (log.isDebugEnabled())
        {
            log.debug("Auth options initialized");
        }
        J2sshConnectionFactory cf = new J2sshConnectionFactory();
        cf.setConnectTimeout(CONNECT_TIMEOUT_MS);
        cf.setKexTimeout(KEY_EXCHANGE_TIMEOUT_MS);
        cf.setSoTimeout(SOCKET_TIMEOUT_MS);
        Connection sshConnection = cf.openConnection(host, DEFAULT_PORT, authOptions);
        if (sshConnection.isClosed())
        {
            throw new Exception("Connection is closed");
        }
        else {
            if (log.isDebugEnabled())
            {
                log.debug("Connection opened");
            }
        }
        return sshConnection;
    }

    public static void uploadFile(String host, String from, String to, String login, String pass) throws Exception {
        Connection sshConnection = connect(host, login, pass);
        uploadFile(sshConnection,from,to);
    }
    public static void uploadFile(Connection sshConnection, String from, String to) throws Exception {
        ScpSession scpSession=sshConnection.openScpSession();
        scpSession.put(from, to, false);
        sshConnection.close();
    }
}
