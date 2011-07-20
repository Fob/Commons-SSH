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
package net.sf.commons.ssh.impl.j2ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPFileAttributes;
import net.sf.commons.ssh.session.SFTPSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.SftpClient;

/**
 * @author Egor Ivanov (crackcraft at gmail dot com)
 * @since 1.2
 */
class J2sshSFTPSession implements SFTPSession
{
    static SFTPFileAttributes convertFileAttributes(
	    final com.sshtools.j2ssh.sftp.FileAttributes a) {
	return new SFTPFileAttributes() {

	    public long getAccessedTime() {
		return a.getAccessedTime().longValue();
	    }

	    public long getGID() {
		return a.getGID().longValue();
	    }

	    public long getModifiedTime() {
		return a.getModifiedTime().longValue();
	    }

	    public long getPermissions() {
		return a.getPermissions().longValue();
	    }

	    public long getSize() {
		return a.getSize().longValue();
	    }

	    public long getUID() {
		return a.getUID().longValue();
	    }

	    public boolean isBlock() {
		return a.isBlock();
	    }

	    public boolean isCharacter() {
		return a.isCharacter();
	    }

	    public boolean isDirectory() {
		return a.isDirectory();
	    }

	    public boolean isFifo() {
		return a.isFifo();
	    }

	    public boolean isFile() {
		return a.isFile();
	    }

	    public boolean isLink() {
		return a.isLink();
	    }

	    public boolean isSocket() {
		return a.isSocket();
	    }
	};
    }

    private static List convertFileList(List files) {
	ArrayList out = new ArrayList(files.size());
	for (Iterator it = files.iterator(); it.hasNext();) {
	    final com.sshtools.j2ssh.sftp.SftpFile f = (com.sshtools.j2ssh.sftp.SftpFile) it
		    .next();
	    out.add(new SFTPFile() {
		public String getAbsolutePath() {
		    return f.getAbsolutePath();
		}

		public SFTPFileAttributes getAttributes() {
		    return convertFileAttributes(f.getAttributes());
		}

		public String getName() {
		    return f.getFilename();
		}

	    });
	}
	return out;
    }

    private final Log log = LogFactory.getLog(this.getClass());

    private final SftpClient sftp;

    J2sshSFTPSession(final SftpClient sftp) {
	log.trace("<init>");
	this.sftp = sftp;
    }

    public void cd(String dir) throws IOException {
	sftp.cd(dir);
    }

    public void chgrp(int gid, String path) throws IOException {
	sftp.chgrp(gid, path);
    }

    // public void mkdirs(String dir) {
    // sftp.mkdirs(dir);
    // }

    public void chmod(int permissions, String path) throws IOException {
	sftp.chmod(permissions, path);
    }

    public void chown(int uid, String path) throws IOException {
	sftp.chown(uid, path);
    }

    public void close() throws IOException {
	sftp.quit();
    }

    public void get(String path) throws IOException {
	sftp.get(path);
    }

    public void get(String remote, OutputStream local) throws IOException {
	sftp.get(remote, local);
    }

    public void get(String remote, String local) throws IOException {
	sftp.get(remote, local);
    }

    public String getAbsolutePath(String path) throws IOException {
	return sftp.getAbsolutePath(path);
    }

    public boolean isClosed() throws IOException {
	return sftp.isClosed();
    }

    public void lcd(String path) throws IOException {
	sftp.lcd(path);
    }

    public String lpwd() {
	return sftp.lpwd();
    }

    public List ls() throws IOException {
	return convertFileList(sftp.ls());
    }

    public List ls(String path) throws IOException {
	return convertFileList(sftp.ls(path));
    }

    public void mkdir(String dir) throws IOException {
	sftp.mkdir(dir);
    }

    public void put(InputStream in, String remote) throws IOException {
	sftp.put(in, remote);
    }

    public void put(String local) throws IOException {
	sftp.put(local);
    }

    public void put(String local, String remote) throws IOException {
	sftp.put(local, remote);
    }

    // public void rm(String path, boolean force, boolean recurse) throws
    // IOException {
    // sftp.rm(path, force, recurse);
    // }

    public String pwd() {
	return sftp.pwd();
    }

    public void rename(String oldpath, String newpath) throws IOException {
	sftp.rename(oldpath, newpath);
    }

    public void rm(String path) throws IOException {
	sftp.rm(path);
    }

    public SFTPFileAttributes stat(String path) throws IOException {
	return convertFileAttributes(sftp.stat(path));
    }

    public void symlink(String path, String link) throws IOException {
	sftp.symlink(path, link);
    }

    public void umask(int umask) {
	sftp.umask(umask);
    }
}
