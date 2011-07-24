package net.sf.commons.ssh.errors;


import java.util.Collection;

public interface ErrorHolder
{
    ErrorLevel getStatus();
    Collection<Error> getAllErrors();
    Collection<Error> getSelfErrors();
    Collection<ErrorHolder> getChildrenHolders();
}
