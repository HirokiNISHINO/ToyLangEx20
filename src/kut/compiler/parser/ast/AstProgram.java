package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.symboltable.ExprType;

public class AstProgram extends AstNode 
{

	
	/**
	 * child node
	 */
	protected AstNode child;
	
	/**
	 * @param node
	 * @param platform
	 */
	public AstProgram(AstNode child)
	{
		this.child = child;
	}

	
	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		child.preprocessStringLiterals(gen);
	}
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "program:");
		child.printTree(indent + 1);
	}

	


	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{
		//process top-level function.
		gen.resetLocalVariableTable();
		this.child.preprocessLocalVariables(gen);
		gen.assignLocalVariableIndices();
		
		this.checkTypes(gen);

		int sfeSize = gen.getStackFrameExtensionSize();
		

		gen.printCode();
		gen.printCode();

		// extend the stack frame.
		gen.printComment("; the top-level function prologue.");
		gen.printCode("push rbp");
		gen.printCode("mov rbp, rsp");
		gen.printCode("sub rsp, " + sfeSize);

		//body of the code
		gen.printCode();
		gen.printComment("; the top-level function body.");
		this.child.cgen(gen);
				
		// rewind the stack frame.
		gen.printCode();
		gen.printComment("; the top-level function epilogue.");
		gen.printCode("mov rsp, rbp");
		gen.printCode("pop rbp");

		return;
	}
	
	/**
	 *
	 */
	public void preprocessGlobalVariables(CodeGenerator gen) 
	{
		this.child.preprocessGlobalVariables(gen);		
	}
	
	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException
	{
		this.child.preprocessLocalVariables(gen);
	}
	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		return this.child.checkTypes(gen);
	}

}
