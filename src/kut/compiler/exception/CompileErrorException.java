/**
 * 
 */
package kut.compiler.exception;

/**
 * @author hnishino
 *
 */
public class CompileErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CompileErrorException() {
	}

	/**
	 * @param message
	 */
	public CompileErrorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CompileErrorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CompileErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CompileErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
