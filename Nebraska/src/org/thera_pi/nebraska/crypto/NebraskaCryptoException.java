package org.thera_pi.nebraska.crypto;

/**
 * This exception is thrown by Nebraska on cryptograpy related errors.
 * It contains the original Exception.
 * 
 * @author bodo
 *
 */
public class NebraskaCryptoException extends NebraskaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -997692134900760353L;

	public NebraskaCryptoException(Exception e) {
		super(e);
	}
}
