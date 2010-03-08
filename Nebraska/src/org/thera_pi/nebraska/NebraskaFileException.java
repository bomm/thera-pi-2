package org.thera_pi.nebraska;

/**
 * This exception is thrown by Nebraska on I/O related errors.
 * It contains the original Exception.
 * 
 * @author bodo
 *
 */
public class NebraskaFileException extends NebraskaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -699118725848566871L;

	public NebraskaFileException(Exception e) {
		super(e);
	}
}
