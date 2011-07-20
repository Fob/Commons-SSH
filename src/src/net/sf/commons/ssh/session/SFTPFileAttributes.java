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

/**
 * @author Egor Ivanov (crackcraft at gmail dot com)
 * @since 1.2
 */
public interface SFTPFileAttributes
{

    long getAccessedTime();

    long getGID();

    long getModifiedTime();

    long getPermissions();

    long getSize();

    long getUID();

    boolean isBlock();

    boolean isCharacter();

    boolean isDirectory();

    boolean isFifo();

    boolean isFile();

    boolean isLink();

    boolean isSocket();
}
