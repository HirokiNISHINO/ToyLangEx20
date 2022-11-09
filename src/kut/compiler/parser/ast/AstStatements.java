package kut.compiler.parser.ast;

import java.io.IOException;
import java.util.Vector;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.symboltable.ExprType;

public class AstStatements extends AstNode 
{
	protected Vector<AstNode> statements;
	
	/**
	 * @param node
	 * @param platform
	 */
	public AstStatements()
	{
		this.statements = new Vector<AstNode>();
	}


	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		for (AstNode s: statements) {
			s.preprocessStringLiterals(gen);
		}
	}
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "statements:");
		for (AstNode s: statements) {
			s.printTree(indent + 1);
		}
	}
	
	
	/**
	 * @param statement
	 */
	public void addStatement(AstNode statement)
	{
		this.statements.add(statement);
	}
	
	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{
		for (AstNode n: statements) {
			n.cgen(gen);
		}
		return;
	}
	
	/**
	 *
	 */
	public void preprocessGlobalVariables(CodeGenerator gen) 
	{
		for (AstNode n: statements) {
			n.preprocessGlobalVariables(gen);
		}
	}
	
	
	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException
	{
		for (AstNode n: statements) {
			n.preprocessLocalVariables(gen);
		}
	}
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		for (AstNode n: statements) {
			n.checkTypes(gen);
		}
		return ExprType.VOID;
	}
	
}
