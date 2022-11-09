package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;
import kut.compiler.symboltable.ExprType;

public class AstBinOp extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstNode	lhs;
	protected AstNode	rhs;
	
	protected ExprType rtype;
	protected ExprType ltype;
	
	/**
	 * @param t
	 */
	public AstBinOp(AstNode lhs, AstNode rhs, Token t)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		this.t = t;
	}
	
	/**
	 * @param gen
	 */
	public void preprocessStringLiterals(CodeGenerator gen) {
		lhs.preprocessStringLiterals(gen);
		rhs.preprocessStringLiterals(gen);
		return;
	}
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "binop:" + t);
		lhs.printTree(indent + 1);
		rhs.printTree(indent + 1);
	}

	
	/**
	 *
	 */
	public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
	{
		rtype = rhs.checkTypes(gen);
		ltype = lhs.checkTypes(gen);
		
		boolean rtypeok = (rtype == ExprType.INT || rtype == ExprType.DOUBLE);
		boolean ltypeok = (ltype == ExprType.INT || ltype == ExprType.DOUBLE);
		
		if (rtypeok != true || ltypeok != true) {
			throw new CompileErrorException("invalid binary operation (only integer and double values can be used). : " + t.toString());
		}

		if (rtype == ExprType.DOUBLE || ltype == ExprType.DOUBLE) {
			return ExprType.DOUBLE;
		}
		return ExprType.INT;
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{	
		lhs.cgen(gen);
		gen.printCode("push rax");
		rhs.cgen(gen);

		if (ltype == ExprType.INT && rtype == ExprType.INT) {
			this.opIntegerInteger(gen);
		}
		else if (ltype == ExprType.DOUBLE && rtype == ExprType.INT) {
			this.opDoubleInteger(gen);
		}
		else if (ltype == ExprType.INT && rtype == ExprType.DOUBLE) {
			this.opIntegerDouble(gen);
		}
		else if (ltype == ExprType.DOUBLE && rtype == ExprType.DOUBLE) {
			this.opDoubleDouble(gen);
		}
		else {
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");	
		}
		return;
	}
	
	/**
	 * @param gen
	 * @throws IOException
	 * @throws CompileErrorException
	 */
	protected void opDoubleDouble(CodeGenerator gen) throws IOException, CompileErrorException
	{
		gen.printCode("movq xmm1, rax");
		gen.printCode("pop rax");		
		gen.printCode("movq xmm0, rax");

		switch(t.getC())
		{
		case '+':
			gen.printCode("addsd xmm0, xmm1");
			break;
			
		case '-':
			gen.printCode("subsd xmm0, xmm1");
			break;
			
		case '*':
			gen.printCode("mulsd xmm0, xmm1");
			break;
			
		case '/':
			gen.printCode("divsd xmm0, xmm1");
			break;
						
		default:
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");	
		}
		gen.printCode("movq rax, xmm0");
		
		return;
	}
	/**
	 * @param gen
	 * @throws IOException
	 * @throws CompileErrorException
	 */
	protected void opIntegerDouble(CodeGenerator gen) throws IOException, CompileErrorException
	{
		gen.printCode("movq xmm1, rax");
		gen.printCode("pop rax");		
		gen.printCode("cvtsi2sd xmm0, rax");

		switch(t.getC())
		{
		case '+':
			gen.printCode("addsd xmm0, xmm1");
			break;
			
		case '-':
			gen.printCode("subsd xmm0, xmm1");
			break;
			
		case '*':
			gen.printCode("mulsd xmm0, xmm1");
			break;
			
		case '/':
			gen.printCode("divsd xmm0, xmm1");
			break;
						
		default:
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");	
		}
		gen.printCode("movq rax, xmm0");
		
		return;
	}
	
	/**
	 * @param gen
	 * @throws IOException
	 * @throws CompileErrorException
	 */
	protected void opDoubleInteger(CodeGenerator gen) throws IOException, CompileErrorException
	{
		gen.printCode("cvtsi2sd xmm1, rax");
		gen.printCode("pop rax");		
		gen.printCode("movq xmm0, rax");

		switch(t.getC())
		{
		case '+':
			gen.printCode("addsd xmm0, xmm1");
			break;
			
		case '-':
			gen.printCode("subsd xmm0, xmm1");
			break;
			
		case '*':
			gen.printCode("mulsd xmm0, xmm1");
			break;
			
		case '/':
			gen.printCode("divsd xmm0, xmm1");
			break;
						
		default:
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");	
		}
		gen.printCode("movq rax, xmm0");
		
		return;
	}
	
	
	/**
	 * @param gen
	 * @throws IOException
	 * @throws CompileErrorException
	 */
	protected void opIntegerInteger(CodeGenerator gen) throws IOException, CompileErrorException
	{
		switch(t.getC())
		{
		case '+':
			gen.printCode("add rax, [rsp]");
			gen.printCode("add rsp, 8");
			break;
			
		case '-':
			gen.printCode("mov rbx, rax");
			gen.printCode("pop rax");
			gen.printCode("sub rax, rbx");
			break;
			
		case '*':
			gen.printCode("imul rax, [rsp]");
			gen.printCode("add rsp, 8");
			break;
			
		case '/':
			gen.printCode("mov rbx, rax");
			gen.printCode("mov rdx, 0");
			gen.printCode("mov rax, [rsp]");
			gen.printCode("add rsp, 8");
			gen.printCode("idiv rbx");
			break;
						
		default:
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");	
		}
		return;	
	}
	

	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException
	{
		this.lhs.preprocessLocalVariables(gen);
		this.rhs.preprocessLocalVariables(gen);
	}


}
