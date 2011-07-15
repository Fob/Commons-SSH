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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Internal representation of factories information
 * 
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
public class Description {

    private static String getFromElement(Element element,
	    String subElementName, boolean required) {
	NodeList nodeList = element.getElementsByTagName(subElementName);
	if (nodeList.getLength() == 0) {
	    if (!required)
		return null;

	    throw new RuntimeException(
		    "SSH connection factory description element '"
			    + element.getNodeName()
			    + "' has no child with name '" + subElementName
			    + "' which is required");
	}

	NodeList children = nodeList.item(0).getChildNodes();
	StringBuffer value = new StringBuffer();
	for (int i = 0; i < children.getLength(); i++) {
	    Node node = children.item(i);
	    if (node instanceof Text) {
		value.append(node.getNodeValue());
	    }
	}

	return value.toString();
    }

    static Description loadDescription(Element element) {
	Description description = new Description();

	description.className = getFromElement(element, "class-name", true); //$NON-NLS-1$
	description.name = getFromElement(element, "name", true); //$NON-NLS-1$
	description.license = getFromElement(element, "license", false); //$NON-NLS-1$
	description.url = getFromElement(element, "url", true); //$NON-NLS-1$

	return description;
    }

    private String className;

    private String license;

    private String name;

    private String url;

    private Description() {
	// internal only
    }

    /**
     * Returns information where to download library
     * 
     * @return information where to download library
     */
    public String dumpInfo() {
	return "SSH library implementation '" + name + "' is avaiable under "
		+ license + " and can be downloaded from " + url + ".";
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Description other = (Description) obj;
	if (className == null) {
	    if (other.className != null)
		return false;
	} else if (!className.equals(other.className))
	    return false;
	return true;
    }

    /**
     * @return the className
     */
    public String getClassName() {
	return className;
    }

    /**
     * @return the license
     */
    public String getLicense() {
	return license;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
	return url;
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((className == null) ? 0 : className.hashCode());
	return result;
    }

    public String toString() {
	return name + " [" + className + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
