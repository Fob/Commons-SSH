package net.sf.commons.ssh.options;

/**
 * Created by IntelliJ IDEA.
 * User: Fob
 * Date: 19.07.11
 * Time: 21:43
 * To change this template use File | Settings | File Templates.
 */
public class IllegalPropertyException extends Exception{
    public IllegalPropertyException( String key, Object value) {
        super("Illigel property key='"+key+"' value='"+value+"'");
    }
}
