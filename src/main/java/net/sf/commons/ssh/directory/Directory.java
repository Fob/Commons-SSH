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

import static net.sf.commons.ssh.directory.XmlUtil.getFromElement;

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
    private final Map<String, String> oldClassNames;

    @SuppressWarnings("unchecked")
    Directory() {
        Map<String, Description> descriptions;
        Map<String, String> oldClassNames;

        try {
            descriptions = Collections.unmodifiableMap(load());
        } catch (Exception exc) {
            descriptions = Collections.EMPTY_MAP;

            log.error(
                    "Unable to load factories directory: " + exc.getMessage(),
                    exc);
        }

        try {
            oldClassNames = Collections.unmodifiableMap(loadOldClassNames());
        } catch (Exception e) {
            oldClassNames = Collections.EMPTY_MAP;
            log.error(
                    "Unable to load old class names of factories: " + e.getMessage(),
                    e);
        }

        this.descriptions = descriptions;
        this.oldClassNames = oldClassNames;
    }

    /**
     * Returns read-only list of {@link Description}s
     *
     * @return read-only list of {@link Description}s
     */
    public Collection<Description> getDescriptions() {
        return Collections.unmodifiableCollection(descriptions.values());
    }

    /**
     * Put custom connectors to directory.
     *
     * @param classes List of classes implements {@link net.sf.commons.ssh.connector.Connector}
     * @return return associated collection od {@link Description}
     */
    public Collection<Description> getDescriptions(Set<String> classes) {
        if (classes == null)
            return getDescriptions();
        Set<Description> result = new HashSet<Description>(classes.size());
        for (String cls : classes) {
            Description description = descriptions.get(cls);
            if (description == null) {
                //try to find connector by old class name
                description = descriptions.get(getNewClassByOld(cls));
            }
            if (description == null) {
                description = new Description(cls);
            }
            result.add(description);
        }
        return result;
    }

    /**
     * Get new class name of factory by old one
     *
     * @param oldClass old class name
     * @return new class name, or {@code oldClass} if there are no mappings for this class
     */
    private String getNewClassByOld(String oldClass) {
        if (oldClassNames.containsKey(oldClass))
            return oldClassNames.get(oldClass);
        return oldClass;
    }


    //load descriptions
    private Map<String, Description> load() throws ParserConfigurationException, SAXException,
            IOException {
        final Document directoryDocument = getDocument("directory.xml");
        final NodeList factories = directoryDocument
                .getElementsByTagName("factory"); //$NON-NLS-1$

        //sort by priority
        SortedSet<Description> factoriesSet = new TreeSet<Description>(new Comparator<Description>() {
            @Override
            public int compare(Description o1, Description o2) {
                return o1.getPriority().compareTo(o2.getPriority());
            }
        });
        for (int i = 0; i < factories.getLength(); i++) {
            final Element element = (Element) factories.item(i);
            final Description description = Description
                    .loadDescription(element);
            factoriesSet.add(description);
        }

        final Map<String, Description> result = new LinkedHashMap<String, Description>(factories.getLength());
        for (Description description : factoriesSet) {
            result.put(description.getClassName(), description);
        }
        return result;
    }

    private Map<String, String> loadOldClassNames() throws IOException, SAXException, ParserConfigurationException {
        final Document directoryDocument = getDocument("factory-compatibility.xml");
        final NodeList classNames = directoryDocument
                .getElementsByTagName("factory"); //$NON-NLS-1$
        HashMap<String, String> result = new HashMap<String, String>(classNames.getLength());
        for (int i = 0; i < classNames.getLength(); i++) {
            final Element element = (Element) classNames.item(i);
            result.put(
                    getFromElement(element, "compat-class-name", true),
                    getFromElement(element, "class-name", true));
        }
        return result;
    }

    private Document getDocument(String resource) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);

        final DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();

        return documentBuilder
                .parse(Directory.class.getResource(resource).toString());
    }


}
