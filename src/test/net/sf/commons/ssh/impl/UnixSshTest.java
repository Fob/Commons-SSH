package net.sf.commons.ssh.impl;

import net.sf.commons.ssh.ConnectorResolvingException;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.options.impl.MapConfigurable;
import net.sf.commons.ssh.session.ShellSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrey on 30.03.16.
 */
public class UnixSshTest {
    public static void main(String[] args) throws IOException {
        final Log log = LogFactory.getLog(UnixSshTest.class);

        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SHELL);

        MapConfigurable mapProperties = new MapConfigurable();


        Connector connector;

        try {
            connector = Manager.getInstance().newConnector(
                    "net.sf.commons.ssh.impl.ussh.UnixSshConnector",
                    features, mapProperties);
        } catch (ConnectorResolvingException e) {
            //log.stream("Can't lookup available connector", e);
            throw new IOException("Can't lookup available connector", e);
        }

        Connection connection = connector.createConnection();

        ConnectionPropertiesBuilder builder = ConnectionPropertiesBuilder.getInstance();
        builder.setHost(connection, "devapp046");
        builder.setPort(connection, 22);
        PasswordPropertiesBuilder passPropBuilder = PasswordPropertiesBuilder.getInstance();
        passPropBuilder.setLogin(connection, "LOGIN"); //TODO: write actual login
        passPropBuilder.setPassword(connection, "PASSWORD".getBytes()); //TODO: write actual password
        try {
            connection.connect(true);
            ShellSession shellSession = connection.createShellSession();
            new Thread(new StreamReader(shellSession.getInputStream(), false)).start();
            new Thread(new StreamReader(shellSession.getErrorStream(), true)).start();
            new Thread(new StreamWriter(shellSession.getOutputStream())).start();
            LogUtils.info(log, "Close connection after 4 seconds");
            Thread.sleep(4000);
            connection.close();
            // java.io.StreamReader inputStream = shellSession.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class StreamReader implements Runnable {
        private BufferedReader stream;
        private final boolean asError;
        private boolean isStopped = false;
        final Log log = LogFactory.getLog(StreamReader.class);

        public void stop() {
            isStopped = true;
            Thread.currentThread().interrupt();
        }

        private StreamReader(java.io.InputStream stream, boolean asError) {
            this.asError = asError;
            this.stream = new BufferedReader(new InputStreamReader(stream));
        }

        public void run() {
            try {
                String input;
                while ((input = stream.readLine()) != null) {
                    if (isStopped)
                        break;
                    if (asError)
                        LogUtils.error(log, input);
                    else
                        LogUtils.info(log, input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static class StreamWriter implements Runnable {

        private OutputStream stream;
        final Log log = LogFactory.getLog(StreamWriter.class);

        private StreamWriter(OutputStream stream) {

            this.stream = stream;
        }

        @Override
        public void run() {
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//                Writer writer = new OutputStreamWriter(stream);
//                String input;
//                PrintWriter printWriter = new PrintWriter(writer);
//                while ((input = reader.readLine()) != null) {
//                    printWriter.write(input);
//                    printWriter.flush();
//                    Thread.currentThread().interrupt();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                LogUtils.info(log, "--- sleep 1000 msec ---");
                Thread.sleep(1000);
                LogUtils.info(log, "--- send ls ---");
                stream.write("ls\n\r".getBytes());
                stream.flush();
                LogUtils.info(log, "--- sleep 1000 msec ---");
                Thread.sleep(1000);
                stream.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
