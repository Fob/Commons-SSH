package net.sf.commons.ssh;

import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.directory.Description;
import net.sf.commons.ssh.directory.Directory;
import net.sf.commons.ssh.options.impl.MapConfigurable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.KeyAgreement;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class set global library preferences and create Connector by features.
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
public final class Manager {
    private static final Log log = LogFactory.getLog(Manager.class);
    //to be compatible with 1.x version
    public static final java.lang.String PROPERTY_NAME = "net.sf.commons.ssh.ConnectionFactory";
    private static Manager instance = null;

    /**
     * creating manager instance,setting security providers
     */
    protected Manager() {
        log.trace("register BC provider");
        try {
            if (java.security.Security.getProvider("BC") == null) {
                java.security.Security.addProvider(new BouncyCastleProvider());
                MessageDigest.getInstance("MD5", "BC");
                KeyAgreement.getInstance("DH", "BC");

            }
        } catch (Exception e) {
            throw new UnexpectedRuntimeException("Registration BC Security provider failed", e);
        }

    }

    /**
     * Getting Manager singleton instance.
     *
     * @return Manager
     */
    public synchronized static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }

        return instance;
    }

    /**
     * Use factory from system property if it exists
     * or otherwise search connector in {@link Directory} by feature and create it.
     *
     * @param features   founded {@link Connector} should supports all {@link Feature} from this collection.
     * @param properties initial {@link net.sf.commons.ssh.options.Properties} for {@link Connector}
     * @return Instance of {@link Connector}
     * @throws ConnectorResolvingException {@link ConnectorResolvingException} if connector haven't been created.
     */
    public Connector newConnector(@NotNull Set<Feature> features, @Nullable net.sf.commons.ssh.options.Properties properties) throws ConnectorResolvingException {
        String defaultFactoryClass = getFactoryFromProperty();
        if (defaultFactoryClass != null)
            return newConnector(defaultFactoryClass, features, properties);
        return newConnector(Directory.getInstance().getDescriptions(), features, properties);
    }

    /**
     * Check connector by features and create it.
     *
     * @param connectorClass connector class to create. It should implements {@link Connector}
     * @param features       {@link Connector} should supports all {@link Feature} from this collection.
     * @param properties     initial {@link net.sf.commons.ssh.options.Properties} for {@link Connector}
     * @return Instance of {@link Connector}
     * @throws ConnectorResolvingException {@link ConnectorResolvingException} if connector haven't been created.
     */
    public Connector newConnector(@NotNull String connectorClass, @NotNull Set<Feature> features, @Nullable net.sf.commons.ssh.options.Properties properties)
            throws ConnectorResolvingException {
        return newConnector(new HashSet<String>(Arrays.asList(connectorClass)), features, properties);
    }

    /**
     * Check connector by features and create it.
     *
     * @param connectorClass connector class to create. It should implements {@link Connector}
     * @param features       {@link Connector} should supports all {@link Feature} from this collection.
     * @param properties     initial {@link net.sf.commons.ssh.options.Properties} for {@link Connector}
     * @return Instance of {@link Connector}
     * @throws ConnectorResolvingException {@link ConnectorResolvingException} if connector haven't been created.
     */
    public Connector newConnector(@NotNull String connectorClass, @NotNull Feature[] features, @Nullable net.sf.commons.ssh.options.Properties properties)
            throws ConnectorResolvingException {
        return newConnector(new HashSet<String>(Arrays.asList(connectorClass)), new HashSet<Feature>(Arrays.asList(features)), properties);
    }

    /**
     * Chose connector by features and create it.
     *
     * @param classes    connector classes to chose from.
     * @param features   chosen {@link Connector} should supports all {@link Feature} from this collection.
     * @param properties properties initial {@link net.sf.commons.ssh.options.Properties} for {@link Connector}
     * @return Instance of {@link Connector}
     * @throws ConnectorResolvingException {@link ConnectorResolvingException} if connector haven't been created.
     */
    public Connector newConnector(@NotNull Set<String> classes, @NotNull Set<Feature> features, @Nullable net.sf.commons.ssh.options.Properties properties)
            throws ConnectorResolvingException {
        return newConnector(Directory.getInstance().getDescriptions(classes), features, properties);
    }

    /**
     * Chose connector by features and create it.
     *
     * @param classes    connector classes to chose from.
     * @param features   chosen {@link Connector} should supports all {@link Feature} from this collection.
     * @param properties properties initial {@link net.sf.commons.ssh.options.Properties} for {@link Connector}
     * @return Instance of {@link Connector}
     * @throws ConnectorResolvingException {@link ConnectorResolvingException} if connector haven't been created.
     */
    public Connector newConnector(@NotNull String[] classes, @NotNull Feature[] features, @Nullable net.sf.commons.ssh.options.Properties properties)
            throws ConnectorResolvingException {
        return newConnector(new HashSet<String>(Arrays.asList(classes)), new HashSet<Feature>(Arrays.asList(features)), properties);
    }

    //create connector method
    @SuppressWarnings("unchecked")
    private Connector newConnector(Collection<Description> classes, Set<Feature> features, @Nullable net.sf.commons.ssh.options.Properties properties)
            throws ConnectorResolvingException {
        LogUtils.debug(log, "Try to create Connector with features {1} from: \n{0}", classes, features);
        ConnectorResolvingException resolvingException = new ConnectorResolvingException(features);
        for (Description description : classes) {
            try {
                Class<? extends Connector> connectorClass = (Class<? extends Connector>) Class.forName(description
                        .getClassName());
                Set<Feature> featuresSet = new HashSet<Feature>();
                SupportedFeatures supportedFeatures = connectorClass.getAnnotation(SupportedFeatures.class);
                if (supportedFeatures == null)
                    LogUtils.info(log, "connector {0} doesn't supports any features", description);
                else
                    featuresSet.addAll(Arrays.asList(supportedFeatures.value()));
                //check features
                if (!featuresSet.containsAll(features)) {
                    LogUtils.info(log, "Connector {0}\n support features {1} \nrequired features {2}", description, featuresSet, features);
                    resolvingException.addDescription(description, MessageFormat
                            .format("    support features {1}\n    required features {2}", description, featuresSet, features));
                    continue;
                }
                //set default properties
                if (properties == null) {
                    properties = new MapConfigurable();
                }
                return connectorClass.getConstructor(net.sf.commons.ssh.options.Properties.class).newInstance(properties);
            } catch (ClassCastException e) {
                LogUtils.info(log, e, "Connector should implements Connector");
                resolvingException.addDescription(description, e.getMessage());
            } catch (Exception e) {

                Throwable cause = e;
                if (e instanceof InvocationTargetException) {
                    cause = e.getCause();
                }
                LogUtils.info(log, cause, "Can''t load connector:\n{0}", description.dumpInfo());
                resolvingException.addDescription(description, e.getMessage());
            }
        }
        // no connector loaded
        if (log.isInfoEnabled())
            log.info(resolvingException.getMessage());
        throw resolvingException;
    }

    private static String getFactoryFromProperty() {
        String factoryName = (String) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty(PROPERTY_NAME);
                    }
                });
        return factoryName;
    }


}
