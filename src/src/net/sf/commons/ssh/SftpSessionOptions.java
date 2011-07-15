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
 * @since 1.2
 * @author Egor Ivanov (crackcraft at gmail dot com)
 */
public class SftpSessionOptions {
    public int defaultPermissions;
    public String localCurrentDirectory;
    public String remoteCurrentDirectory;

    public SftpSessionOptions() {
	this(null, null, 0022);
    }

    /**
     * Creates an instance of {@link SftpSessionOptions} with umask = 0022
     * 
     * @param remoteCurrentDirectory
     *            Remote current working directory
     * @param localCurrentDirectory
     *            Local current working directory
     */
    public SftpSessionOptions(String remoteCurrentDirectory,
	    String localCurrentDirectory) {
	this(remoteCurrentDirectory, localCurrentDirectory, 0022);
    }

    /**
     * Creates an instance of {@link SftpSessionOptions}
     * 
     * @param cwd
     *            Remote current working directory
     * @param lcwd
     *            Local current working directory
     * @param defaultPermissions
     *            Default permissions
     */
    public SftpSessionOptions(String cwd, String lcwd, int defaultPermissions) {
	this.remoteCurrentDirectory = cwd;
	this.localCurrentDirectory = lcwd;
	this.defaultPermissions = defaultPermissions;
    }

}
