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




public enum Feature
{
    AUTH_CREDENTIALS,
    AUTH_PUBLICKEY,
    SESSION_EXEC,
    SESSION_SFTP,
    SESSION_SHELL ,
    SESSION_SUBSYSTEM,
    SOCKET_TIMEOUT,
    CONNECTION_TIMEOUT ,
    ASYNCHRONOUS,
    ERROR_STREAM,
    SSH1,
    SSH2;
}
