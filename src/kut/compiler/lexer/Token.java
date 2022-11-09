package kut.compiler.lexer;

public class Token 
{
	/**
	 * token class
	 */
	protected int		c;
	
	
	/**
	 * lexeme
	 */
	protected String	l;
	
	
	/**
	 * 
	 */
	protected int		lineNo;
	
	/**
	 * @param c
	 * @param l
	 */
	public Token(int c, String l, int lineNo) {
		this.c = c;
		this.l = l;
		this.lineNo = lineNo;
	}

	/**
	 * @return
	 */
	public int getC() {
		return c;
	}

	/**
	 * @return
	 */
	public String getL() {
		return l;
	}
	
	
	/**
	 * @return
	 */
	public int getLineNo() {
		return this.lineNo;
	}
	
	/**
	 *
	 */
	public String toString() {
		return TokenClass.getTokenClassString(c) + "(" + l + ")@line:" + this.getLineNo();
	}
	
}
