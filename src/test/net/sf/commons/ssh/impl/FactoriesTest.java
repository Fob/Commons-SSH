package net.sf.commons.ssh.impl;

import net.sf.commons.ssh.ConnectorResolvingException;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.directory.Description;
import net.sf.commons.ssh.directory.Directory;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by anku0315 on 25.04.2016.
 */
public class FactoriesTest {
    @Test
    public void testPriority() {
        Collection<Description> descriptions = Directory.getInstance().getDescriptions();
        int priority = 0;
        for (Description description : descriptions) {
            assertEquals(++priority, description.getPriority().intValue());
        }
    }

    @Test
    public void testSystemProperty() throws ConnectorResolvingException {
        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SHELL);
        String defFactory = "net.sf.commons.ssh.impl.jsch.JSCHConnector";
        System.setProperty(Manager.PROPERTY_NAME, defFactory);
        Connector connector = Manager.getInstance().newConnector(features, null);
        assertEquals(defFactory, connector.getClass().getName());
    }

    @Test
    public void testOldClassMapping() throws ConnectorResolvingException {
        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SHELL);
        String factory = "net.sf.commons.ssh.jsch.JschConnectionFactory";
        Connector connector = Manager.getInstance().newConnector(factory, features, null);
        assertEquals(connector.getClass().getName(), "net.sf.commons.ssh.impl.jsch.JSCHConnector");
    }
}
