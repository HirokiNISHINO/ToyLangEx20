package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;

public class AstLocal extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstIdentifier	varname	;
	protected Token			type	;
	
	/**
	 * @param t
	 */
	public AstLocal(AstIdentifier varname, Token type, Token t)
	{
		this.varname 	= varname;
		this.type		= type;
		this.t 			= t;
	}
	
	

	/**
	 *
	 */
	@Override
	protected void printTree(int indent) {
		this.println(indent, "local (type: " + type + ")");
		varname.printTree(indent + 1);
	}



	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws CompileErrorException, IOException
	{	
		int idx = gen.getStackIndexOfLocalVariable(this.getVarName().getIdentifier());
		gen.printCode("mov rax, 0");
		gen.printCode("mov [rbp + " + idx + "], rax");
		return; 
	}
	
	/**
	 * @return
	 */
	public AstIdentifier getVarName() {
		return this.varname;
	}
	
	/**
	 * @return
	 */
	public Token getTypeToken() {
		return this.type;
	}

	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen)  throws SyntaxErrorException
	{
		gen.declareLocalVariable(this);
		return;
	}
	

	/**
	 *
	 */
	public String toString() {
		return this.getVarName().getIdentifier() + ":" + this.type;
	}

	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		return ExprType.VOID;
	}
}
