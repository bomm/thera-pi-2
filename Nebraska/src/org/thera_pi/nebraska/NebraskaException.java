package org.thera_pi.nebraska;

/**
 * This class is used to group the different exceptions thrown by the
 * Nebraska library. It is never thrown directly.
 * 
 * @author bodo
 *
 */
public abstract class NebraskaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1961125927196980211L;

	public NebraskaException(Exception e) {
		super(e);
	}
}
