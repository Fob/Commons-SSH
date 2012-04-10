/*
 * Copyright 2009-2009 CommonsSSH Project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.commons.ssh.directory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Contains predefined library descriptions
 *
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
public class Directory {

    private static final class InstanceHolder {
        static Directory directory = new Directory();
    }

    private static final Log log = LogFactory.getLog(Directory.class);

    /**
     * Returns instance of {@link Directory}
     *
     * @return instance of {@link Directory}
     */
    public static Directory getInstance() {
        return InstanceHolder.directory;
    }

    private final Map<String, Description> descriptions;

    @SuppressWarnings("unchecked")
    Directory() {
        Map<String, Description> descriptions;

        try {
            descriptions = Collections.unmodifiableMap(load());
        }
        catch (Exception exc) {
            descriptions = Collections.EMPTY_MAP;

            log.error(
                    "Unable to load factories directory: " + exc.getMessage(),
                    exc);
        }

        this.descriptions = descriptions;
    }

    /**
     * Returns read-only list of {@link Description}s
     *
     * @return read-only list of {@link Description}s
     */
    public Collection<Description> getDescriptions() {
        return descriptions.values();
    }

    /**
     * Put custom connectors to directory.
     *
     * @param classes List of classes implements {@link net.sf.commons.ssh.connector.Connector}
     * @return return associated collection od {@link Description}
     */
    public Set<Description> getDescriptions(Set<String> classes) {
        Set<Description> result = new HashSet<Description>(classes.size());
        for (String cls : classes) {
            Description description = descriptions.get(cls);
            if (description == null)
                description = new Description(cls);
            result.add(description);
        }
        return result;
    }

    //load descriptions
    private Map<String, Description> load() throws ParserConfigurationException, SAXException,
            IOException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);

        final DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();

        final Document directoryDocument = documentBuilder
                .parse(Directory.class.getResource("directory.xml").toString());

        final NodeList factories = directoryDocument
                .getElementsByTagName("factory"); //$NON-NLS-1$
        final Map<String, Description> result = new HashMap<String, Description>(factories.getLength());
        for (int i = 0; i < factories.getLength(); i++) {
            final Element element = (Element) factories.item(i);
            final Description description = Description
                    .loadDescription(element);
            result.put(description.getClassName(), description);
        }

        return result;
    }
}
