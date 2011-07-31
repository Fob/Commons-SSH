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
package net.sf.commons.ssh.auth;

/**
 * Password authentication options
 * 
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
public class PasswordAuthenticationOptions extends AuthenticationOptions {
    /**
     * @since 1.0
     */
    protected String password;

    /**
     * Creates new instance of {@link PasswordAuthenticationOptions}
     * 
     * @param login
     *            user name
     * @since 1.0
     */
    public PasswordAuthenticationOptions(String login) {
	if (login == null)
	    throw new IllegalArgumentException("login is null");
	if (login.length() == 0)
	    throw new IllegalArgumentException("login is empty");

	this.login = login;
    }

    /**
     * Creates new instance of {@link PasswordAuthenticationOptions}
     * 
     * @param login
     *            user name
     * @param password
     *            user password
     * @since 1.0
     */
    public PasswordAuthenticationOptions(String login, String password) {
	this(login);
	this.password = password;
    }

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}