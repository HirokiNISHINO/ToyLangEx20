package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;

public class AstStringLiteral extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	/**
	 * @param t
	 */
	public AstStringLiteral(Token t)
	{
		this.t = t;
	}
	

	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		String literal = t.getL();
		gen.foundStringLiteral(literal);
		return;
	}
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "string literal:" + t);
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException
	{	
		String litereal = t.getL();
		String label = gen.getStringLiteralLabel(litereal);

		gen.printCode("lea rax, [rel " + label + "]");
	}
	
	
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		return ExprType.STRING;
	}


}
