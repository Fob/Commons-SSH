/**
 * 
 */
package net.sf.commons.ssh.common;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public enum Status
{
	UNKNOWN,
	CREATED,
	CONNECTING,
	CONNECTED,
	AUTHENTICATING,
	AUTHENTICATED,
	OPENING,
	OPENNED,
	INPROGRESS,
	CLOSING,
	CLOSED;
	
	public boolean between(Status s1,Status s2)
	{
		return compareTo(s1)>0 && compareTo(s2)<0;		
	}
	public boolean betweenLeft(Status s1,Status s2)
	{
		return compareTo(s1)>=0 && compareTo(s2)<0;		
	}
	public boolean betweenRight(Status s1,Status s2)
	{
		return compareTo(s1)>0 && compareTo(s2)<=0;		
	}
	public boolean betweenBoth(Status s1,Status s2)
	{
		return compareTo(s1)>=0 && compareTo(s2)<=0;		
	}
}
