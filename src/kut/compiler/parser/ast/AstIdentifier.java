package kut.compiler.parser.ast;


import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;
import kut.compiler.symboltable.SymbolType;

public class AstIdentifier extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	/**
	 * @param t
	 */
	public AstIdentifier(Token t)
	{
		this.t = t;
	}

	
	/**
	 * @return
	 */
	public String getIdentifier()
	{
		return t.getL();
	}
	
	/**
	 *
	 */
	@Override
	protected void printTree(int indent) {
		this.println(indent, "identifier:" + t.getL());
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{	
		String id = this.getIdentifier();
		
		SymbolType st = gen.getSymbolType(id);
		if (st == SymbolType.GlobalVariable) {
			gen.printCode("mov rax, [ rel " + gen.getGlobalVariableLabel(t.getL()) + "]");
			return;
		}
		
		if (st == SymbolType.LocalVariable) {
			int idx = gen.getStackIndexOfLocalVariable(id);
			gen.printCode("mov rax, [rbp + " + idx + "]");
			return;
		}
		
		throw new CompileErrorException("unknown symbol type: " + this.getIdentifier());
	}
	
	

	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen)  throws SyntaxErrorException
	{
		String id = this.getIdentifier();

		SymbolType st = gen.getSymbolType(id);
		if (st == SymbolType.Unknown){
			throw new SyntaxErrorException("undeclared local variable found: " + this.getIdentifier());
		}
		
		return;
	}

	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		ExprType t = gen.getVariableType(getIdentifier());
		return t;
	}
}
