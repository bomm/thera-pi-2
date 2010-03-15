package org.thera_pi.nebraska.crypto;

/**
 * This exception is thrown when NebraskaKeystore is not fully initialized for the
 * requested operation, e.g. when a constructor with a reduced set of parameters
 * is called and the operation need unspecified parameters.
 * 
 * To avoid this exception choose a different constructor or use the 
 * setters to specify the missing parameters.
 * 
 * @author bodo
 *
 */
public class NebraskaNotInitializedException extends NebraskaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3680835701397200859L;

	public NebraskaNotInitializedException(Exception e) {
		super(e);
	}
}
