package net.sf.commons.ssh.impl.j2ssh;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anku0315 on 29.04.2016.
 */
public class J2SSHSftpSession extends AbstractSession implements SFTPSession {
    private final Log log = LogFactory.getLog(this.getClass());
    private SftpClient sftpClient;
    public J2SSHSftpSession(Properties properties, SshClient connection) {
        super(properties);
        try {
            sftpClient = connection.openSftpClient();
            SftpSessionPropertiesBuilder sftpProps = new SftpSessionPropertiesBuilder();
            sftpClient.umask(sftpProps.getDefaultPermissions(properties));

            String rcd = sftpProps.getRemoteCurrentDirectory(properties);
            if (rcd != null)
                sftpClient.cd(rcd);
            String lcd = sftpProps.getLocalCurrentDirectory(properties);
            if (lcd != null)
                sftpClient.lcd(lcd);
        } catch (IOException e) {
            log.error("can't create j2ssh sftp session");
            throw new UnexpectedRuntimeException(e.getMessage(), e);
        }
        setContainerStatus(Status.CREATED);
    }



    private static List<SFTPFile> convertFileList(List files) {
        List<SFTPFile> out = new ArrayList<>(files.size());
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            final com.sshtools.j2ssh.sftp.SftpFile f = (com.sshtools.j2ssh.sftp.SftpFile) it
                    .next();
            // skip current and previous directory
            if (f.getFilename().equals(".")) continue;
            if (f.getFilename().equals("..")) continue;
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



    @Override
    protected void openImpl() throws IOException {
        //NOTHING?
    }

    @Override
    protected void closeImpl() throws IOException {
        //TODO?
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public void cd(String dir) throws IOException {
        sftpClient.cd(dir);
    }

    @Override
    public void chgrp(int gid, String path) throws IOException {
        sftpClient.chgrp(gid, path);
    }

    @Override
    public void chmod(int permissions, String path) throws IOException {
        sftpClient.chmod(permissions, path);
    }

    @Override
    public void chown(int uid, String path) throws IOException {
        sftpClient.chown(uid, path);
    }

    @Override
    public void get(String path) throws IOException {
        sftpClient.get(path);
    }

    @Override
    public void get(String remote, OutputStream local) throws IOException {
        sftpClient.get(remote, local);
    }

    @Override
    public void get(String remote, String local) throws IOException {
        sftpClient.get(remote, local);
    }

    @Override
    public String getAbsolutePath(String path) throws IOException {
        return sftpClient.getAbsolutePath(path);
    }

    @Override
    public void lcd(String path) throws IOException {
        sftpClient.lcd(path);
    }

    @Override
    public String lpwd() {
        return sftpClient.lpwd();
    }

    @Override
    public List<SFTPFile> ls() throws IOException {
        return convertFileList(sftpClient.ls());
    }

    @Override
    public List<SFTPFile> ls(String path) throws IOException {
        return convertFileList(sftpClient.ls(path));
    }

    @Override
    public void mkdir(String dir) throws IOException {
        sftpClient.mkdir(dir);
    }

    @Override
    public void put(InputStream in, String remote) throws IOException {
        sftpClient.put(in, remote);
    }

    @Override
    public void put(String local) throws IOException {
        sftpClient.put(local);
    }

    @Override
    public void put(String local, String remote) throws IOException {
        sftpClient.put(local, remote);
    }

    @Override
    public String pwd() throws IOException {
        return sftpClient.pwd();
    }

    @Override
    public void rename(String oldpath, String newpath) throws IOException {
        sftpClient.rename(oldpath, newpath);
    }

    @Override
    public void rm(String path) throws IOException {
        sftpClient.rm(path);
    }

    @Override
    public SFTPFileAttributes stat(String path) throws IOException {
        return convertFileAttributes(sftpClient.stat(path));
    }

    @Override
    public void symlink(String path, String link) throws IOException {
        sftpClient.symlink(path, link);
    }

    @Override
    public void umask(int umask) {
        sftpClient.umask(umask);
    }

    @Override
    public boolean isOpened() {
        //TODO: using EventListener
        return true;
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

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
}
