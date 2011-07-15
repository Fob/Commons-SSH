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
package net.sf.commons.ssh;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.commons.ssh.directory.Description;
import net.sf.commons.ssh.directory.Directory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
class ConnectionFactoryResolver {

    private static final Log log = LogFactory
	    .getLog(ConnectionFactoryResolver.class);

    final static String PROPERTY_NAME = ConnectionFactory.class.getName();

    private static String getFactoryFromProperty() {
	String factoryName = (String) AccessController
		.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			return System.getProperty(PROPERTY_NAME);
		    }
		});
	return factoryName;
    }

    static ConnectionFactory resolve(Set requiredFeatures)
    {
        ConnectionFactory result;

        String defaultFactoryClassName = getFactoryFromProperty();
        if (defaultFactoryClassName != null
                && defaultFactoryClassName.trim().length() > 0)
        {
            if (log.isInfoEnabled())
            {
                log.info("Trying to instantiate default ConnectionFactory '"
                        + defaultFactoryClassName + "' from system property '"
                        + PROPERTY_NAME + "'");
            }

            result = tryToLoad(defaultFactoryClassName, requiredFeatures);

            if (result != null)
            {
                return result;
            }
        }

        List descriptions = Directory.getInstance().getDecriptions();
        for (final Iterator iter = descriptions.iterator(); iter.hasNext();)
        {
            final Description description = (Description) iter.next();

            if (log.isInfoEnabled())
            {
                log.info("Trying to instantiate ConnectionFactory of "
                        + description);
            }

            result = tryToLoad(description.getClassName(), requiredFeatures);

            if (result != null)
            {
                return result;
            }
        }

        // no factories loaded

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("Either unable to load any of SSH connection factories or none of them does support all required features. "
                        + "SSH implementation libraries supported by Commons SSH:");
        for (final Iterator iter = descriptions.iterator(); iter.hasNext();)
        {
            final Description description = (Description) iter.next();

            stringBuffer.append("\n* ");
            stringBuffer.append(description.dumpInfo());
        }
        final String message = stringBuffer.toString();
        log.warn(message);

        throw new RuntimeException(message);
    }

    static ConnectionFactory resolve(String factory, Set requiredFeatures)
    {
        if(factory==null)
            return resolve(requiredFeatures);
        ConnectionFactory result = tryToLoad(factory, requiredFeatures);
        if (result != null)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("failed to load connection factory '"+factory+"'");
        }
    }

    /**
     * @param factoryClassName
     * @return
     */
    private static ConnectionFactory tryToLoad(final String factoryClassName,
	    Set requiredFeatures) {

	final Class cls;
	try {
	    cls = (Class) AccessController
		    .doPrivileged(new PrivilegedExceptionAction() {
			public Object run() throws ClassNotFoundException {
			    return Class.forName(factoryClassName, true, Thread
				    .currentThread().getContextClassLoader());
			}
		    });
	} catch (Throwable exc) {
	    log.info("Unable to instantiate ConnectionFactory from "
		    + factoryClassName);
	    log.debug("Unable to load connection factory class: "
		    + exc.getMessage());
	    return null;
	}

	if (log.isDebugEnabled())
	    log.debug("Class '" + factoryClassName + "' resolved");

	if (!ConnectionFactory.class.isAssignableFrom(cls)) {
	    log.warn("Class '" + cls.getName()
		    + "' can't be used as SSH connection factory "
		    + "because it doesn't extends class "
		    + ConnectionFactory.class.getName());
	    return null;
	}

	final ConnectionFactory connectionFactory;
	try {
	    connectionFactory = (ConnectionFactory) cls.newInstance();
	} catch (Throwable exc) {
	    log.info("Unable to instantiate ConnectionFactory from '"
		    + factoryClassName + "'");
	    log.debug("Unable to instantiate ConnectionFactory: "
		    + exc.getMessage(), exc);
	    return null;
	}

	if (log.isInfoEnabled())
	    log.info("Connection factory instantiated from '"
		    + factoryClassName + "'");

	log.debug("Checking if required features "
		+ "are supported by this connection factory: "
		+ requiredFeatures);

	for (Iterator iter = requiredFeatures.iterator(); iter.hasNext();) {
	    final String feature = (String) iter.next();
	    if (!connectionFactory.isFeatureSupported(feature)) {
		log.debug("Connection factory '" + factoryClassName
			+ "' doesn't support required feature '" + feature
			+ "' and won't be used");
		return null;
	    }
	}

	log.debug("Connection factory '" + factoryClassName
		+ "' supports all required features.");

	return connectionFactory;
    }
}
