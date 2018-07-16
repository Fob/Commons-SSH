package net.sf.commons.ssh.impl.sshj;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;
import net.schmizz.sshj.xfer.*;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPFileAttributes;
import net.sf.commons.ssh.session.SFTPSession;
import org.apache.sshd.common.util.io.IoUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SSHJSftpSession extends AbstractSession implements SFTPSession {
    private SFTPClient sftpClient;
    private StatefulSFTPClient statefulSftpClient;

    private SFTPFileAttributes convertFileAttributes(final FileAttributes attr){
        return new SFTPFileAttributes() {
            @Override
            public long getAccessedTime() {
                return attr.getAtime();
            }
            @Override
            public long getGID() {
                return attr.getGID();
            }
            @Override
            public long getModifiedTime() {
                return attr.getMtime();
            }
            @Override
            public long getPermissions() {
                return attr.getPermissions().size();
            }
            @Override
            public long getSize() {
                return attr.getSize();
            }
            @Override
            public long getUID() {
                return attr.getUID();
            }
            @Override
            public boolean isBlock() {
                return attr.getMode().getType() == FileMode.Type.BLOCK_SPECIAL;
            }
            @Override
            public boolean isCharacter() {
                return attr.getMode().getType() == FileMode.Type.CHAR_SPECIAL;
            }
            @Override
            public boolean isDirectory() {
                return attr.getMode().getType() == FileMode.Type.DIRECTORY;
            }
            @Override
            public boolean isFifo() {
                return attr.getMode().getType() == FileMode.Type.FIFO_SPECIAL;
            }
            @Override
            public boolean isFile() {
                return attr.getMode().getType() == FileMode.Type.REGULAR;
            }
            @Override
            public boolean isLink() {
                return attr.getMode().getType() == FileMode.Type.SYMLINK;
            }
            @Override
            public boolean isSocket() {
                return attr.getMode().getType() == FileMode.Type.SOCKET_SPECIAL;
            }
        };
    }

    public SSHJSftpSession(Properties properties, SSHClient sshClient) {
        super(properties);
        try {
            sftpClient = sshClient.newSFTPClient();
            statefulSftpClient = new StatefulSFTPClient(sftpClient.getSFTPEngine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException {
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }

    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public void cd(String dir) throws IOException {
       statefulSftpClient.cd(dir);
    }

    @Override
    public void chgrp(int gid, String path) throws IOException {
        sftpClient.chgrp(path, gid);
    }

    @Override
    public void chmod(int permissions, String path) throws IOException {
        sftpClient.chmod(path, permissions);
    }

    @Override
    public void chown(int uid, String path) throws IOException {
        sftpClient.chown(path, uid);
    }

    @Override
    public void get(String path) throws IOException {
       sftpClient.get(path, path);
    }

    @Override
    public void get(String remote, final OutputStream local) throws IOException {
        sftpClient.get(remote, new InMemoryDestFile() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                return local;
            }
        });
    }

    @Override
    public void get(String remote, String local) throws IOException {
        sftpClient.get(remote,local);
    }

    @Override
    public String getAbsolutePath(String path) throws IOException {
        return pwd().replaceFirst("/","") + "/" + path;
    }

    @Override
    public void lcd(String path) throws IOException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public String lpwd() {
        return System.getProperty("user.home"); //feature from j2ssh
    }

    @Override
    public List<SFTPFile> ls() throws IOException {
        return convertFileList(statefulSftpClient.ls());
    }

    private List<SFTPFile> convertFileList(List<RemoteResourceInfo> files) {
        ArrayList out = new ArrayList(files.size());
        for (final RemoteResourceInfo file : files ) {
            out.add(new SFTPFile() {
                public String getAbsolutePath() {
                    return file.getPath();
                }

                public SFTPFileAttributes getAttributes() {
                    SFTPFileAttributes attrs = null;
                    try {
                        attrs = convertFileAttributes(file.getAttributes());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return attrs;
                }

                public String getName() {
                    return file.getName();
                }

            });
        }
        return out;
    }

    @Override
    public List ls(String path) throws IOException {
        return statefulSftpClient.ls(path);
    }

    @Override
    public void mkdir(String dir) throws IOException {
        sftpClient.mkdir(dir);
    }

    @Override
    public void put(final InputStream in, String remote) throws IOException {
        byte[] data = IoUtils.toByteArray(in);
        sftpClient.open(remote).write(0, data,0,data.length);
    }

    @Override
    public void put(String local) throws IOException {
        sftpClient.put(local, pwd());
    }

    @Override
    public void put(String local, String remote) throws IOException {
        sftpClient.put(local,remote);
    }

    @Override
    public String pwd() throws IOException {
        return statefulSftpClient.pwd();
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
        sftpClient.symlink(link, path);
    }

    @Override
    public void umask(int umask) {
        throw new UnsupportedOperationException("not supported");
    }


    @Override
    public boolean isOpened() {
        Status status = getContainerStatus();
        return (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }
}
