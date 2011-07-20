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
package net.sf.commons.ssh.impl.jsch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPFileAttributes;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.SftpSessionOptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * @since 1.2
 * @author Egor Ivanov (crackcraft at gmail dot com)
 */
class JschSFTPSession implements SFTPSession
{
    private static final Log log = LogFactory.getLog(JschSFTPSession.class);

    static SFTPFileAttributes convertFileAttributes(final SftpATTRS a) {
	return new SFTPFileAttributes() {

	    public long getAccessedTime() {
		return a.getATime();
	    }

	    public long getGID() {
		return a.getGId();
	    }

	    public long getModifiedTime() {
		return a.getMTime();
	    }

	    public long getPermissions() {
		return a.getPermissions();
	    }

	    public long getSize() {
		return a.getSize();
	    }

	    public long getUID() {
		return a.getUId();
	    }

	    public boolean isBlock() {
		return ((a.getPermissions() & 0xF000) == 0x1000);
	    }

	    public boolean isCharacter() {
		return ((a.getPermissions() & 0xF000) == 0x2000);
	    }

	    public boolean isDirectory() {
		return a.isDir();
	    }

	    public boolean isFifo() {
		return ((a.getPermissions() & 0xF000) == 0x6000);
	    }

	    public boolean isFile() {
		return ((a.getPermissions() & 0xF000) == 0x8000);
	    }

	    public boolean isLink() {
		return a.isLink();
	    }

	    public boolean isSocket() {
		return ((a.getPermissions() & 0xF000) == 0xC000);
	    }
	};
    }

    private static List convertFileList(List files) {
	ArrayList out = new ArrayList(files.size());
	for (Iterator it = files.iterator(); it.hasNext();) {
	    final ChannelSftp.LsEntry f = (ChannelSftp.LsEntry) it.next();
	    out.add(new SFTPFile() {
		public String getAbsolutePath() {
		    return f.getFilename();
		}

		public SFTPFileAttributes getAttributes() {
		    return convertFileAttributes(f.getAttrs());
		}

		public String getName() {
		    return (new File(f.getFilename())).getName();
		}

		public String toString() {
		    return f.getLongname();
		}

	    });
	}
	return out;
    }

    static IOException ioe(Exception ex) {
	IOException ex2 = new IOException(ex.getMessage());
	ex2.setStackTrace(ex.getStackTrace());
	ex2.initCause(ex.getCause());
	return ex2;
    }

    private int umask;

    private final ChannelSftp sftp;

    JschSFTPSession(final ChannelSftp sftp, SftpSessionOptions opts)
	    throws IOException {
	log.trace("<init>");
	this.sftp = sftp;
	umask(opts.defaultPermissions);
	cd((opts.remoteCurrentDirectory != null) ? opts.remoteCurrentDirectory
		: ".");
	lcd((opts.localCurrentDirectory != null) ? opts.localCurrentDirectory
		: ".");
    }

    public void cd(String dir) throws IOException {
	try {
	    sftp.cd(dir);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void chgrp(int gid, String path) throws IOException {
	try {
	    sftp.chgrp(gid, path);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    // public void mkdirs(String dir) {
    // sftp.mkdirs(dir);
    // }

    public void chmod(int permissions, String path) throws IOException {
	try {
	    sftp.chmod(permissions, path);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void chown(int uid, String path) throws IOException {
	try {
	    sftp.chown(uid, path);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void close() throws IOException {
	sftp.quit();
    }

    public void get(String path) throws IOException {
	get(path, path);
    }

    public void get(String remote, OutputStream local) throws IOException {
	try {
	    sftp.get(remote, local);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void get(String remote, String local) throws IOException {
	try {
	    sftp.get(remote, local);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public String getAbsolutePath(String path) throws IOException {
	if (path.startsWith("/")) {
	    return path;
	}
	String rwd = pwd();
	return (rwd.endsWith("/")) ? rwd + path : rwd + "/" + path;
    }

    public boolean isClosed() throws IOException {
	return sftp.isClosed();
    }

    public void lcd(String path) throws IOException {
	try {
	    sftp.lcd(path);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public String lpwd() {
	return sftp.lpwd();
    }

    public List ls() throws IOException {
	return ls(".");
    }

    public List ls(String path) throws IOException {
	try {
	    return convertFileList(sftp.ls(path));
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void mkdir(String dir) throws IOException {
	try {
	    sftp.mkdir(dir);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void put(InputStream in, String remote) throws IOException {
	try {
	    sftp.put(in, remote);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void put(String local) throws IOException {
	try {
	    sftp.put(local);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void put(String local, String remote) throws IOException {
	try {
	    sftp.put(local, remote);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    // public void rm(String path, boolean force, boolean recurse) throws
    // IOException {
    // sftp.rm(path, force, recurse);
    // }

    public String pwd() throws IOException {
        try
        {
            return sftp.pwd();
        }
        catch (SftpException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public void rename(String oldpath, String newpath) throws IOException {
	try {
	    sftp.rename(oldpath, newpath);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void rm(String path) throws IOException {
	try {
	    SftpATTRS a = sftp.stat(path);
	    if (a.isDir()) {
		sftp.rmdir(path);
	    } else {
		sftp.rm(path);
	    }
	} catch (SftpException ex) {
	    throw ioe(ex);
	}

    }

    public void umask(int umask) {
	this.umask = umask;
    }

    public SFTPFileAttributes stat(String path) throws IOException {
	try {
	    return convertFileAttributes(sftp.stat(path));
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

    public void symlink(String path, String link) throws IOException {
	try {
	    sftp.symlink(path, link);
	} catch (SftpException ex) {
	    throw ioe(ex);
	}
    }

}
