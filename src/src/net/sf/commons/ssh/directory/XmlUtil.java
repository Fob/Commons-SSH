package net.sf.commons.ssh.directory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Created by anku0315 on 25.04.2016.
 */
public class XmlUtil {
    public static String getFromElement(Element element,
                                         String subElementName, boolean required)
    {
        NodeList nodeList = element.getElementsByTagName(subElementName);
        if (nodeList.getLength() == 0)
        {
            if (!required)
            {
                return null;
            }

            throw new RuntimeException(
                    "SSH connection factory description element '"
                            + element.getNodeName()
                            + "' has no child with name '" + subElementName
                            + "' which is required");
        }

        NodeList children = nodeList.item(0).getChildNodes();
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node node = children.item(i);
            if (node instanceof Text)
            {
                value.append(node.getNodeValue());
            }
        }

        return value.toString();
    }
}
