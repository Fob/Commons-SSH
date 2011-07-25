package net.sf.commons.ssh.connector;


import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.connection.Connection;


import java.util.Set;

public interface Connector extends Container
{
    Connection createConnection();
    Connection openConnection(String host,int port,AuthenticationOptions auth);
    Set<Feature> getSupportedFeatures();
}
