package net.sf.commons.ssh.connector;


import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.options.Properties;


import java.util.Set;

public interface Connector extends Container
{
    Connection createConnection();
    Connection openConnection(String host,int port,Properties connectionProperties) throws ConnectionException,AuthenticationException,HostCheckingException;
    Set<Feature> getSupportedFeatures();
}
