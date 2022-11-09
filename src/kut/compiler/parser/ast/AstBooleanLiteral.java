package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;

public class AstBooleanLiteral extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	/**
	 * @param t
	 */
	public AstBooleanLiteral(Token t)
	{
		this.t = t;
	}
	
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "boolean literal:" + t);
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException
	{	
		if (t.getL().equals("true")) {
			gen.printCode("mov rax, 1");
		}
		else {
			gen.printCode("mov rax, 0");
		}
		return;
	}
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		return ExprType.BOOLEAN;
	}

}
