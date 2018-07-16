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

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import static net.sf.commons.ssh.directory.XmlUtil.getFromElement;

/**
 * Internal representation of factories information
 *
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @author anku0315 - add factory priority
 * @since 1.0
 */
public class Description
{
    static Description loadDescription(Element element)
    {
        Description description = new Description();

        description.className = StringUtils.trim(getFromElement(element, "class-name", true)); //$NON-NLS-1$
        description.name = getFromElement(element, "name", true); //$NON-NLS-1$
        description.license = getFromElement(element, "license", false); //$NON-NLS-1$
        description.url = getFromElement(element, "url", true); //$NON-NLS-1$
        try {
            description.priority = Integer.valueOf(getFromElement(element, "priority", false));
        } catch (NumberFormatException e) {
            description.priority = 100;
        }
        return description;
    }

    private String className;

    private String license;

    private String name;

    private String url;

    private Integer priority;

    private Description()
    {
        // internal only
    }

    public Description(String connectorClass)
    {
        this();
        name = "Unknown Library";
        url = "[Unknown]";
        license = "[Unknown]";
        className = connectorClass;
        priority = 100; //by default factory is not important
    }
    /**
     * Returns information where to download library
     *
     * @return information where to download library
     */
    public String dumpInfo()
    {
        return "SSH library implementation '" + name + "' is avaiable under "
                + license + " and can be downloaded from " + url + ".";
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Description other = (Description) obj;
        if (className == null)
        {
            if (other.className != null)
            {
                return false;
            }
        }
        else if (!className.equals(other.className))
        {
            return false;
        }
        return true;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @return the license
     */
    public String getLicense()
    {
        return license;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    public String toString()
    {
        return name + " [" + className + "]";
    }


}
