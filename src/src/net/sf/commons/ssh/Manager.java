package net.sf.commons.ssh;


import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.directory.Description;
import net.sf.commons.ssh.directory.Directory;
import net.sf.commons.ssh.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public final class Manager
{
    private static final Log log = LogFactory.getLog(Manager.class);
    private static Manager instance = null;

    private synchronized static Manager getInstance()
    {
        if (instance == null)
        {
            instance = new Manager();
        }

        return instance;
    }

    public Connector newConnector(Collection<Feature> features)
    {
        return newConnector(Directory.getInstance().getDescriptions(), features);
    }

    public Connector newConnector(String factory, Collection<Feature> features)
    {
        return newConnector(Arrays.asList(factory), features);
    }

    public Connector newConnector(List<String> factories, Collection<Feature> features)
    {
        return newConnector(Directory.getInstance().getDescriptions(factories), features);
    }

    private Connector newConnector(Collection<Description> factories, Collection<Feature> features)
    {
        Connector result = null;
        LogUtils.info(log, "Try to create Connector with features {1} from: \n{0}", factories, features);

        for (Description description : factories)
        {
            try
            {
                Class<? extends Connector> connectorClass = (Class<? extends Connector>) Class.forName(description.getClassName());
                SupportedFeatures supportedFeatures= connectorClass.getAnnotation(SupportedFeatures.class);
                if(supportedFeatures == null)
                    throw new Exception("Connector doesn't support any features");
                List<Feature> featuresSet = Arrays.asList(supportedFeatures.value());
                if(!featuresSet.containsAll(features))
                    throw new Exception("Connector "+description.toString()+" \nsupport features: "+featuresSet+
                            "\nrequired features: "+features);
            }
            catch (Exception e)
            {
                LogUtils.info(log,"Can't load library:\n{0}",description.dumpInfo(),e);
            }
        }
        // no connector loaded
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("Either unable to load any of SSH connectors or none of them does support all required features. "
                        + "SSH implementation libraries supported by Commons SSH:");
        for (final Iterator<Description> iterator = factories.iterator(); iterator.hasNext();)
        {
            final Description description = iterator.next();

            stringBuffer.append("\n* ");
            stringBuffer.append(description.dumpInfo());
        }
        final String message = stringBuffer.toString();
        log.error(message);
        throw new ConnectorResolvingException(factories,features);
    }

}
