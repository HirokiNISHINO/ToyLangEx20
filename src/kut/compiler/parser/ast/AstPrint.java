package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;

public class AstPrint extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstNode	expr;
	protected ExprType	etype;
	
	/**
	 * @param t
	 */
	public AstPrint(AstNode expr, Token t)
	{
		this.expr 	= expr;
		this.t 		= t;
	}
	
	
	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		expr.preprocessStringLiterals(gen);
	}

	/**
	 *
	 */
	@Override
	protected void printTree(int indent) {
		this.println(indent, "print : ");
		expr.printTree(indent + 1);
	}


	
	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{			
		this.expr.cgen(gen);
		if (etype == ExprType.INT) {
			gen.printCode("call " + gen.getPrintIntLabel());			
		}
		else if (etype == ExprType.STRING) {
			gen.printCode("call " + gen.getPrintStringLabel());			
			
		}
		else if (etype == ExprType.DOUBLE) {
			gen.printCode("call " + gen.getPrintDoubleLabel());			
			
		}
		else if (etype == ExprType.BOOLEAN) {
			gen.printCode("call " + gen.getPrintBooleanLabel());			
			
		}
		else {
			throw new CompileErrorException("the code shouldn't reach here. a bug.");
		}
		return; 
	}
	

	/**
	 *
	 */
	/**
	 *
	 */
	public void preprocessGlobalVariables(CodeGenerator gen)
	{
		return;
	}
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		etype = expr.checkTypes(gen);
		return ExprType.VOID;
	}
}
