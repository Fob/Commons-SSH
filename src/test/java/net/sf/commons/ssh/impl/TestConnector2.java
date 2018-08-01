package net.sf.commons.ssh.impl;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorHolder;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.*;
import net.sf.commons.ssh.options.Properties;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * date: 09.04.12
 * Time: 0:01
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
@SupportedFeatures({Feature.AUTH_NONE, Feature.SOCKET_TIMEOUT})
public class TestConnector2 implements Connector {

    public TestConnector2(Properties properties) {
    }


    @Override
    public Connection createConnection() {
        return null;
    }

    @Override
    public Connection openConnection(String host, int port, Properties connectionProperties) throws ConnectionException, AuthenticationException, HostCheckingException {
        return null;
    }

    @Override
    public Set<Feature> getSupportedFeatures() {
        return null;
    }

    @Override
    public Status getContainerStatus() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isClosedWithChildren() {
        return false;
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public void clearClosed() {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setProperty(String key, Object value) {

    }

    @Override
    public void updateFrom(Properties properties) throws CloneNotSupportedException {

    }

    @Override
    public ErrorLevel getStatus() {
        return null;
    }

    @Override
    public Collection<Error> getAllErrors() {
        return null;
    }

    @Override
    public Collection<Error> getSelfErrors() {
        return null;
    }

    @Override
    public Collection<ErrorHolder> getChildrenHolders() {
        return null;
    }

    @Override
    public Selector createSelector() {
        return null;
    }

    @Override
    public void addEventHandler(EventHandler handler) {

    }

    @Override
    public void removeEventHandler(EventHandler handler) {

    }

    @Override
    public EventHandler addListener(EventListener listener, EventFilter filter, HandlerType type) {
        return null;
    }

    @Override
    public EventHandler addListener(EventListener listener, EventFilter filter) {
        return null;
    }

    @Override
    public ProducerType getProducerType() {
        return null;
    }

    @Override
    public Object getProperty(String key) {
        return null;
    }

    @Override
    public void includeDefault(Properties configurable) {

    }
}
