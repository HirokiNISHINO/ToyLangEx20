/**
 * 
 */
package kut.compiler.exception;

/**
 * @author hnishino
 *
 */
public class LexerErrorException extends CompileErrorException {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public LexerErrorException() {
	}

	/**
	 * @param message
	 */
	public LexerErrorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LexerErrorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LexerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public LexerErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
