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

/**
 * @since 1.0
 * @see ConnectionFactory#isFeatureSupported(String)
 * @see ConnectionFactory#getSupportedFeatures()
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
public interface Features {

    /**
     * If connection factory supports authentication by specified username and
     * password
     */
    final String AUTH_CREDENTIALS = PasswordAuthenticationOptions.class
	    .getName();

    /**
     * If connection factory supports authentication by public key
     */
    final String AUTH_PUBLICKEY = PublicKeyAuthenticationOptions.class
	    .getName();

    /**
     * If connection factory supports command execution session
     */
    final String SESSION_EXEC = ExecSession.class.getName();

    /**
     * If connection factory supports SFTP session
     */
    final String SESSION_SFTP = SftpSession.class.getName();

    /**
     * If connection factory supports shell session
     */
    final String SESSION_SHELL = ShellSession.class.getName();

    final String SOCKET_TIMEOUT = "SocketTimeout";
    final String CONNECTION_TIMEOUT = "ConnectionTimeout";
}
