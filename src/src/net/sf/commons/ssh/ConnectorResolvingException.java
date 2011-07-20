package net.sf.commons.ssh;


import net.sf.commons.ssh.directory.Description;

import java.util.Collection;

public class ConnectorResolvingException extends RuntimeException
{
    private Collection<Description> descriptions;
    private Collection<Feature> features;

    public ConnectorResolvingException(Collection<Description> descriptions, Collection<Feature> features)
    {
        this.descriptions = descriptions;
        this.features = features;
    }

    @Override
    public String getMessage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Either unable to load any of SSH connection factories or none of them does support all required features:\n ").
                append(features).append(".\nRequested SSH implementation libraries:");
        for (Description description : descriptions)
        {
            builder.append("\n").append(description.dumpInfo());
        }
        return builder.toString();
    }
}
