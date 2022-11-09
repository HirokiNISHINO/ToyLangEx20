package kut.compiler.parser.ast;


import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.symboltable.ExprType;

public class AstEmptyStatement extends AstNode 
{

	
	/**
	 * @param t
	 */
	public AstEmptyStatement()
	{
	}


	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "empty statement: ;");
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen)
	{	
		return;
	}
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		return ExprType.VOID;
	}

}
