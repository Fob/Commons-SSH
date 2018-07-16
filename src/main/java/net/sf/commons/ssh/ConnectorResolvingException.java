package net.sf.commons.ssh;

import net.sf.commons.ssh.directory.Description;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Throwing when manager can't create connector.
 */
public class ConnectorResolvingException extends Exception {

    private static final long serialVersionUID = 7526657334650450285L;
    private Map<Description, String> descriptions = new HashMap<Description, String>();
    private Set<Feature> features;

    public ConnectorResolvingException(Set<Feature> features) {
        this.features = features;
    }

    //add description for internal usages
    void addDescription(Description description, String message) {
        descriptions.put(description, message);
    }

    /**
     * Return set of {@link net.sf.commons.ssh.connector.Connector} descriptions{@link Description} with fail messages. Detailed exceptions will be in logs.
     *
     * @return Map of descriptions - messages
     */
    public Map<Description, String> getDescriptions() {
        return descriptions;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(
                "Either unable to load any of SSH connection factories or none of them does support all required features:\n ")
                .append(features).append(".\nRequested SSH implementation libraries:");
        for (Map.Entry<Description, String> en : descriptions.entrySet())
            builder.append("\n").append(en.getKey().dumpInfo()).append("\nCaused by:\n").append(en.getValue());
        builder.append("\nSee logs for exception details.");
        return builder.toString();
    }

}
