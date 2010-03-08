package org.thera_pi.nebraska;

public abstract class NebraskaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1961125927196980211L;

	public NebraskaException(Exception e) {
		super(e);
	}
}
