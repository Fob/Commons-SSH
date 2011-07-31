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

import java.io.File;

/**
 * Key authentication options
 * 
 * @since 1.1
 * @author Egor Ivanov (crackcraft at gmail dot com)
 */
public class PublicKeyAuthenticationOptions extends AuthenticationOptions {

    /**
     * @since 1.1
     */
    protected String keyfile;

    /**
     * @since 1.1
     */
    protected String phrase;

    /**
     * Creates new instance of
     * {@link PublicKeyAuthenticationOptions}
     * 
     * @param login
     *            user name
     * @param keyfile
     *            path to private keyfile
     * @since 1.1
     */
    public PublicKeyAuthenticationOptions(String login, String keyfile) {
	if (login == null)
	    throw new IllegalArgumentException("login is null");
	if (login.length() == 0)
	    throw new IllegalArgumentException("login is empty");
	if (keyfile == null)
	    throw new IllegalArgumentException("keyfile is null");

	// check file
	File file = new File(keyfile);
	if (!file.exists())
	    throw new IllegalArgumentException("Private key file '"
		    + file.getAbsolutePath() + "' not found");

	if (!file.isFile())
	    throw new IllegalArgumentException("Private key '"
		    + file.getAbsolutePath() + "' is not a file");

	if (!file.canRead())
	    throw new IllegalArgumentException("Private key '"
		    + file.getAbsolutePath() + "' can't be read");

	this.login = login;
	this.keyfile = keyfile;
    }

    /**
     * Creates new instance of
     * {@link PublicKeyAuthenticationOptions}
     * 
     * @param login
     *            user name
     * @param keyfile
     *            path to private keyfile
     * @param phrase
     *            passphrase for private keyfile
     * @since 1.1
     */
    public PublicKeyAuthenticationOptions(String login, String keyfile,
	    String phrase) {
	this(login, keyfile);
	this.phrase = phrase;
    }

	public String getKeyfile()
	{
		return keyfile;
	}

	public void setKeyfile(String keyfile)
	{
		this.keyfile = keyfile;
	}

	public String getPhrase()
	{
		return phrase;
	}

	public void setPhrase(String phrase)
	{
		this.phrase = phrase;
	}
    
    
}
