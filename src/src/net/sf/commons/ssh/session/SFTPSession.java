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
package net.sf.commons.ssh.session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @since 1.2
 * @see net.sf.commons.ssh.Feature#SESSION_SFTP
 */
public interface SFTPSession extends Session {

    /**
     * Changes the working directory on the remote server.
     * 
     * @param dir
     *            the new working directory
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     * @throws FileNotFoundException
     * 
     */
    void cd(String dir) throws IOException;

    /**
     * Sets the group ID for the file or directory.
     * 
     * @param gid
     *            the numeric group id for the new group
     * @param path
     *            the path to the remote file/directory
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     */
    void chgrp(int gid, String path) throws IOException;

    /**
     * Changes the access permissions or modes of the specified file or
     * directory.
     * 
     * Modes determine who can read, change or execute a file.
     * 
     * <blockquote>
     * 
     * <pre>
     * Absolute modes are octal numbers specifying the complete list of
     * attributes for the files; you specify attributes by OR'ing together
     * these bits.
     * 
     * 0400       Individual read
     * 0200       Individual write
     * 0100       Individual execute (or list directory)
     * 0040       Group read
     * 0020       Group write
     * 0010       Group execute
     * 0004       Other read
     * 0002       Other write
     * 0001       Other execute
     * </pre>
     * 
     * </blockquote>
     * 
     * @param permissions
     *            the absolute mode of the file/directory
     * @param path
     *            the path to the file/directory on the remote server
     * 
     * @throws IOException
     *             if an IO error occurs or the file if not found
     */
    void chmod(int permissions, String path) throws IOException;

    /**
     * Sets the user ID to owner for the file or directory.
     * 
     * @param uid
     *            numeric user id of the new owner
     * @param path
     *            the path to the remote file/directory
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     */
    void chown(int uid, String path) throws IOException;

    // /**
    // * Create a directory or set of directories. This method will not fail
    // even
    // * if the directories exist. It is advisable to test whether the directory
    // * exists before attempting an operation by using the <code>stat</code>
    // * method to return the directories attributes.
    // *
    // * @param dir the path of directories to create.
    // */
    // void mkdirs(String dir);

    /**
     * Download the remote file to the local computer.
     * 
     * @param path
     *            the path to the remote file
     * 
     * @throws IOException
     *             if an IO error occurs of the file does not exist
     */
    public void get(String path) throws IOException;

    /**
     * Download the remote file writing it to the specified
     * <code>OutputStream</code>. The OutputStream is closed by this method even
     * if the operation fails.
     * 
     * @param remote
     *            the path/name of the remote file
     * @param local
     *            the OutputStream to write
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     */
    void get(String remote, OutputStream local) throws IOException;

    /**
     * Download the remote file to the local computer. If the paths provided are
     * not absolute the current working directory is used.
     * 
     * @param remote
     *            the path/name of the remote file
     * @param local
     *            the path/name of the local file
     * 
     * @throws IOException
     */
    void get(String remote, String local) throws IOException;

    /**
     * @param path
     * 
     * @return
     * 
     * @throws IOException
     */
    String getAbsolutePath(String path) throws IOException;

    /**
     * Changes the local working directory.
     * 
     * @param path
     *            the path to the new working directory
     * 
     * @throws IOException
     *             if an IO error occurs
     */
    void lcd(String path) throws IOException;

    /**
     * Returns the absolute path to the local working directory.
     * 
     * @return the absolute path of the local working directory.
     */
    String lpwd();

    /**
     * List the contents of the current remote working directory.
     * 
     * @return a list of SFTPFile for the current working directory
     * 
     * @throws IOException
     *             if an IO error occurs
     * 
     * @see SFTPFile
     */
    @SuppressWarnings("rawtypes")
	List<SFTPFile> ls() throws IOException;

    /**
     * List the contents remote directory.
     * 
     * @param path
     *            the path on the remote server to list
     * 
     * @return a list of SFTPFile for the remote directory
     * 
     * @throws IOException
     *             if an IO error occurs
     * 
     * @see SFTPFile
     */
    @SuppressWarnings("rawtypes")
	List ls(String path) throws IOException;

    /**
     * Creates a new directory on the remote server. This method will throw an
     * exception if the directory already exists. To create directories and
     * disregard any errors use the <code>mkdirs</code> method.
     * 
     * @param dir
     *            the name of the new directory
     * 
     * @throws IOException
     *             if an IO error occurs or if the directory already exists
     * 
     */
    void mkdir(String dir) throws IOException;

    /**
     * @param in
     * @param remote
     * 
     * @throws IOException
     */
    void put(InputStream in, String remote) throws IOException;

    /**
     * Upload a file to the remote computer.
     * 
     * @param local
     *            the path/name of the local file
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     * 
     */
    void put(String local) throws IOException;

    /**
     * Upload a file to the remote computer. If the paths provided are not
     * absolute the current working directory is used.
     * 
     * @param local
     *            the path/name of the local file
     * @param remote
     *            the path/name of the destination file
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     */
    void put(String local, String remote) throws IOException;

    /**
     * Returns the absolute path name of the current remote working directory.
     * 
     * @return the absolute path of the remote working directory.
     */
    String pwd() throws IOException;

    /**
     * Rename a file on the remote computer.
     * 
     * @param oldpath
     *            the old path
     * @param newpath
     *            the new path
     * 
     * @throws IOException
     *             if an IO error occurs
     * 
     */
    void rename(String oldpath, String newpath) throws IOException;

    /**
     * Remove a file or directory from the remote computer.
     * 
     * @param path
     *            the path of the remote file/directory
     * 
     * @throws IOException
     *             if an IO error occurs
     */
    void rm(String path) throws IOException;

    /**
     * Returns the attributes of the file from the remote computer.
     * 
     * @param path
     *            the path of the file on the remote computer
     * 
     * @return the attributes
     * 
     * @throws IOException
     *             if an IO error occurs or the file does not exist
     * 
     * @see SFTPFileAttributes
     */
    SFTPFileAttributes stat(String path) throws IOException;

    // /**
    // * @param path
    // * @param force
    // * @param recurse
    // *
    // * @throws IOException
    // */
    // void rm(String path, boolean force, boolean recurse) throws IOException;

    /**
     * Create a symbolic link on the remote computer.
     * 
     * @param path
     *            the path to the existing file
     * @param link
     *            the new link
     * 
     * @throws IOException
     *             if an IO error occurs or the operation is not supported on
     *             the remote platform
     */
    void symlink(String path, String link) throws IOException;

    /**
     * Sets the defaultPermissions used by this client.
     * 
     * @param umask
     */
    void umask(int umask);
}
