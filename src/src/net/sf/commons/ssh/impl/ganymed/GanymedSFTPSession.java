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
package net.sf.commons.ssh.impl.ganymed;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPFileAttributes;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.SftpSessionOptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;

/**
 * @author Egor Ivanov (crackcraft at gmail dot com)
 * @since 1.2
 */
class GanymedSFTPSession implements SFTPSession
{
    static SFTPFileAttributes convertFileAttributes(final SFTPv3FileAttributes a) {
	return new SFTPFileAttributes() {

	    public long getAccessedTime() {
		return (a.atime != null) ? a.atime.longValue() : 0;
	    }

	    public long getGID() {
		return (a.gid != null) ? a.gid.longValue() : 0;
	    }

	    public long getModifiedTime() {
		return (a.mtime != null) ? a.mtime.longValue() : 0;
	    }

	    public long getPermissions() {
		return (a.permissions != null) ? a.permissions.longValue() : 0;
	    }

	    public long getSize() {
		return (a.size != null) ? a.size.longValue() : 0;
	    }

	    public long getUID() {
		return (a.uid != null) ? a.uid.longValue() : 0;
	    }

	    public boolean isBlock() {
		return (a.permissions != null)
			&& ((a.permissions.intValue() & 0xF000) == 0x1000);
	    }

	    public boolean isCharacter() {
		return (a.permissions != null)
			&& ((a.permissions.intValue() & 0xF000) == 0x2000);
	    }

	    public boolean isDirectory() {
		return a.isDirectory();
	    }

	    public boolean isFifo() {
		return (a.permissions != null)
			&& ((a.permissions.intValue() & 0xF000) == 0x6000);
	    }

	    public boolean isFile() {
		return a.isRegularFile();
	    }

	    public boolean isLink() {
		return a.isSymlink();
	    }

	    public boolean isSocket() {
		return (a.permissions != null)
			&& ((a.permissions.intValue() & 0xF000) == 0xC000);
	    }
	};
    }

    private static List convertFileList(List files) {
	ArrayList out = new ArrayList(files.size());
	for (Iterator it = files.iterator(); it.hasNext();) {
	    final SFTPv3DirectoryEntry f = (SFTPv3DirectoryEntry) it.next();
	    out.add(new SFTPFile() {
		public String getAbsolutePath() {
		    return f.filename;
		}

		public SFTPFileAttributes getAttributes() {
		    return convertFileAttributes(f.attributes);
		}

		public String getName() {
		    return (f.filename.lastIndexOf("/") < 0) ? f.filename
			    : f.filename
				    .substring(f.filename.lastIndexOf("/") + 1);
		}
	    });
	}
	return out;
    }

    private final Log log = LogFactory.getLog(this.getClass());
    private String lwd;
    private String rwd;

    private final SFTPv3Client sftp;

    private int umask;

    GanymedSFTPSession(final SFTPv3Client sftp, SftpSessionOptions opts)
	    throws IOException {
	log.trace("<init>");
	this.sftp = sftp;
	umask(opts.defaultPermissions);
	cd((opts.remoteCurrentDirectory != null) ? opts.remoteCurrentDirectory
		: "");
	lcd((opts.localCurrentDirectory != null) ? opts.localCurrentDirectory
		: "");
    }

    public void cd(String dir) throws IOException {
	String rwd2 = sftp.canonicalPath(rpath(dir));
	SFTPv3FileAttributes a = sftp.stat(rwd2);
	if (!a.isDirectory()) {
	    throw new IOException(dir + " is not a directory");
	}

	rwd = rwd2;
    }

    public void chgrp(int gid, String path) throws IOException {
	SFTPv3FileAttributes a = new SFTPv3FileAttributes();
	a.gid = new Integer(gid);
	sftp.setstat(rpath(path), a);
    }

    public void chmod(int permissions, String path) throws IOException {
	SFTPv3FileAttributes a = new SFTPv3FileAttributes();
	a.permissions = new Integer(permissions);
	sftp.setstat(rpath(path), a);
    }

    // public void mkdirs(String dir) {
    // sftp.mkdirs(dir);
    // }

    public void chown(int uid, String path) throws IOException {
	SFTPv3FileAttributes a = new SFTPv3FileAttributes();
	a.uid = new Integer(uid);
	sftp.setstat(rpath(path), a);
    }

    public void close() throws IOException {
	sftp.close();
    }

    public void get(String path) throws IOException {
	get(path, path);
    }

    public void get(String remote, OutputStream local) throws IOException {
	SFTPv3FileHandle h = sftp.openFileRO(rpath(remote));
	byte[] buff = new byte[32768];
	long off = 0;
	while (true) {
	    int res = sftp.read(h, off, buff, 0, buff.length);
	    if (res == -1) {
		break;
	    }
	    if (res > 0) {
		local.write(buff, 0, res);
		off += res;
	    }

	}
	sftp.closeFile(h);
    }

    public void get(String remote, String local) throws IOException {
	if ((local.lastIndexOf('/') == local.length() - 1)
		|| lpath(local).isDirectory()) {
	    int pos = remote.lastIndexOf('/');
	    String filename = (pos >= 0) ? remote.substring(pos + 1) : remote;
	    local = local + '/' + filename;
	}
	File f = lpath(local);
	if (!f.exists()) {
	    f.getParentFile().mkdirs();
	}
	OutputStream os = new FileOutputStream(f);
	try {
	    get(remote, os);
	} finally {
	    os.close();
	}
    }

    public String getAbsolutePath(String path) throws IOException {
	return sftp.canonicalPath(rpath(path));
    }

    public boolean isClosed() throws IOException {
	try {
	    sftp.stat(".");
	} catch (IOException ex) {
	    return true;
	}

	return false;
    }

    public void lcd(String path) throws IOException {
	File f = lpath(path);
	if (!f.isDirectory()) {
	    throw new IOException(path + " is not a directory");
	}
	lwd = f.getCanonicalPath();
    }

    private File lpath(String path) {
	File f = new File(path);
	if (!f.isAbsolute()) {
	    f = new File(((lwd != null) ? lwd : ""), path);
	}
	return f;
    }

    public String lpwd() {
	return lwd;
    }

    public List ls() throws IOException {
	return ls(".");
    }

    public List ls(String path) throws IOException {
	return convertFileList(sftp.ls(rpath(path)));
    }

    public void mkdir(String dir) throws IOException {
	sftp.mkdir(rpath(dir), 0777 ^ umask);
    }

    public void put(InputStream in, String remote) throws IOException {
	SFTPv3FileAttributes a = new SFTPv3FileAttributes();
	a.permissions = new Integer(0777 ^ umask);
	SFTPv3FileHandle h = sftp.createFileTruncate(rpath(remote), a);
	byte[] buff = new byte[32768];
	long off = 0;
	while (true) {
	    int res = in.read(buff);
	    if (res == -1) {
		break;
	    }
	    if (res > 0) {
		sftp.write(h, off, buff, 0, res);
		off += res;
	    }
	}
	sftp.closeFile(h);
    }

    public void put(String local) throws IOException {
	put(local, local);
    }

    public void put(String local, String remote) throws IOException {
	File f = lpath(local);
	if (!f.exists() && !f.isFile()) {
	    throw new IOException("File not found " + f.getCanonicalPath());
	}
	if (remote.lastIndexOf('/') == remote.length() - 1) {
	    remote = remote + f.getName();
	}
	InputStream is = new FileInputStream(f);
	try {
	    put(is, remote);
	} finally {
	    is.close();
	}
    }

    public String pwd() throws IOException {
	return rpath("");
    }

    // public void rm(String path, boolean force, boolean recurse) throws
    // IOException {
    // sftp.rm(path, force, recurse);
    // }

    public void rename(String oldpath, String newpath) throws IOException {
	sftp.mv(rpath(oldpath), rpath(newpath));
    }

    public void rm(String path) throws IOException {
	SFTPv3FileAttributes a = sftp.stat(rpath(path));
	if (a.isDirectory()) {
	    sftp.rmdir(rpath(path));
	} else {
	    sftp.rm(rpath(path));
	}
    }

    private String rpath(String path) throws IOException {
	if (path.startsWith("/")) {
	    return path;
	}
	if (rwd == null) {
	    rwd = sftp.canonicalPath("");
	}
	return rwd + (rwd.endsWith("/") ? "" : "/") + path;
    }

    public SFTPFileAttributes stat(String path) throws IOException {
	return convertFileAttributes(sftp.stat(rpath(path)));
    }

    public void symlink(String path, String link) throws IOException {
	// TODO test relative links.
	// ex: rwd = /home/xxx, path=../yyy link=zzz
	// : /home/xxx/zzz -> ../yyy, NOT -> /home/yyy
	sftp.createSymlink(path, rpath(link));
    }

    public void umask(int umask) {
	this.umask = umask;
    }
}
