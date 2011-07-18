package net.sf.commons.ssh;


public enum SessionType
{
    EXEC,SFTP,SHELL;

    public static class UnknownSessionType extends Exception
    {
        public UnknownSessionType(SessionType type)
        {
            super("Unknown Session type "+type);
        }
    }
}
