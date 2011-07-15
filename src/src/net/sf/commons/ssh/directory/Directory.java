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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
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

    private final List descriptions;

    Directory() {
	List descriptions;

	try {
	    descriptions = Collections.unmodifiableList(load());
	} catch (Exception exc) {
	    descriptions = Collections.EMPTY_LIST;

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
    public List getDecriptions() {
	return descriptions;
    }

    private List load() throws ParserConfigurationException, SAXException,
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
	final List result = new ArrayList(factories.getLength());
	for (int i = 0; i < factories.getLength(); i++) {
	    final Element element = (Element) factories.item(i);
	    final Description description = Description
		    .loadDescription(element);
	    result.add(description);
	}

	return result;
    }
}
