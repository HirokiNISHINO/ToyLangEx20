package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;
import kut.compiler.symboltable.SymbolType;

public class AstAssignment extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstIdentifier	varname	;
	protected AstNode			rhs	;
	
	protected ExprType		ltype	;
	protected ExprType		rtype	;
	
	/**
	 * @param t
	 */
	public AstAssignment(AstIdentifier varname, AstNode rhs, Token t)
	{
		this.varname 	= varname	;
		this.rhs 		= rhs		;
		this.t 			= t			;
	}
	
	
	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		this.rhs.preprocessStringLiterals(gen);
	}
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "assignment:" + t);
		varname.printTree(indent + 1);
		rhs.printTree(indent + 1);
	}

	

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{	
		rhs.cgen(gen);
		String id = this.varname.getIdentifier();		
		SymbolType t = gen.getSymbolType(id);
		
		//need implicit type-casting for the DOUBLE = INT assignment.
		if (ltype == ExprType.DOUBLE && rtype == ExprType.INT) {			
			//type case rax:INT to rax:DOUBLE
			gen.printCode("cvtsi2sd xmm0, rax");
			gen.printCode("movq rax, xmm0");
		}
		
		//DOUBLE = DOUBLE or INT = INT.
		if (t == SymbolType.GlobalVariable) {
			gen.printCode("mov [rel " + gen.getGlobalVariableLabel(id) + "], rax");		
			return;
		}
		
		if (t == SymbolType.LocalVariable) {
			int idx = gen.getStackIndexOfLocalVariable(id);
			gen.printCode("mov [rbp + " + idx + "], rax");
			return;
		}
		
		throw new CompileErrorException("unknown local variable found: " + id);
	}
	

	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		String id = this.varname.getIdentifier();
		
		ltype = gen.getVariableType(id);	
		rtype = rhs.checkTypes(gen);
		
		//assigning a int value to a double variable.
		if (ltype == ExprType.DOUBLE && rtype == ExprType.INT) {
			return ltype;
		}
		
		if (ltype != rtype) {
			throw new CompileErrorException("invalid assignment (types don't match): " + t);
		}
		
		return ltype;
	}
	
	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException
	{
		this.varname.preprocessLocalVariables(gen);
		this.rhs.preprocessLocalVariables(gen);
	}

}
