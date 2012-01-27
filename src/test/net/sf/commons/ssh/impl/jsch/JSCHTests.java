/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

//import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.common.IOUtils;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.ConnectorPropertiesBuilder;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.session.ShellSession;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.sshd.common.util.SecurityUtils;
//import org.junit.Test;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.KBIAuthenticationClient;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class JSCHTests
{

	
	public static void main(String[] arg) throws AuthenticationException, ConnectionException, HostCheckingException, IOException, InterruptedException
	{
		Logger.getRootLogger().setLevel(Level.ALL);
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout("%c %p : %m%n"));
		appender.setWriter(new OutputStreamWriter(System.out));
		Logger.getRootLogger().addAppender(appender);
		Connector connector=null;
		Connection connection=null;
		ShellSession session = null;
		try
		{
			connector = Manager.getInstance().newConnector("net.sf.commons.ssh.impl.sshd.SSHDConnector",
					Arrays.asList(Feature.SSH2,Feature.SYNCHRONOUS,Feature.AUTH_CREDENTIALS,Feature.SESSION_SHELL),null);
			connection = connector.createConnection();
			ConnectionPropertiesBuilder.getInstance().setHost(connection, "devapp046.netcracker.com");
			PasswordPropertiesBuilder.getInstance().setupAuthenticationMethod(connection);
			PasswordPropertiesBuilder.getInstance().setLogin(connection, "netcrk");
			PasswordPropertiesBuilder.getInstance().setPassword(connection, "crknet");
			connection.connect(false);
			connection.authenticate();
			System.out.println(connection.getHostKey());
			session = connection.createShellSession();
			session.open();
			Thread.sleep(1000);
			InputStream st = session.getInputStream();
			OutputStream out = session.getOutputStream();
			out.write("uname -a\n".getBytes());
			Thread.sleep(1000);
			byte[] buffer = new byte[st.available()];
			st.read(buffer);
			System.out.println("output\n"+new String(buffer));
			buffer = new byte[st.available()];
		}
		finally
		{
			IOUtils.close(session);
			IOUtils.close(connection);
			IOUtils.close(connector);
		}
		System.out.println(connector.getAllErrors());
/*		SecurityUtils.isBouncyCastleRegistered();
		SshClient client = new SshClient();
		client.connect("192.168.1.137",22,new IgnoreHostKeyVerification());
		
		PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
		pwd.setUsername("fob");
		pwd.setPassword(new String("0xFD#syhdrtwaGNT".getBytes(),"UTF-8"));
		
		System.out.println(client.authenticate(pwd));
		client.disconnect();*/
	}

}
