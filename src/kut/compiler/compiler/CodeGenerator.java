package kut.compiler.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import kut.compiler.exception.CompileErrorException;
import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.parser.ast.AstNode;
import kut.compiler.parser.ast.AstGlobal;
import kut.compiler.parser.ast.AstLocal;
import kut.compiler.symboltable.ExprType;
import kut.compiler.symboltable.StringLiteralAndLabel;
import kut.compiler.symboltable.SymbolTable;
import kut.compiler.symboltable.SymbolType;

/**
 * @author hnishino
 *
 */
/**
 * @param program
 */
public class CodeGenerator 
{
	protected Platform		platform;
	protected String 		filename;
	protected AstNode		program	;
	protected PrintWriter 	writer	;
	
	protected SymbolTable	symbolTable;
	
	
	/**
	 * @param program
	 * @param filename
	 */
	public CodeGenerator(AstNode program, String filename, Platform platform) 
	{
		this.program 	= program;
		this.filename 	= filename;
		this.platform	= platform;
		
		this.symbolTable = new SymbolTable();
	}	
	
	
	/**
	 * @return
	 */
	public String getPrintIntLabel()
	{
		return "print_int#";
	}
	
	/**
	 * @return
	 */
	public String getPrintStringLabel()
	{
		return "print_string#";
	}
	
	/**
	 * @return
	 */
	public String getPrintDoubleLabel()
	{
		return "print_double#";
	}
	/**
	 * @return
	 */
	public String getPrintBooleanLabel()
	{
		return "print_boolean#";
	}
	
	
	/**
	 * @return
	 */
	public String getExitSysCallNum()
	{
		return (this.platform == Platform.MAC ? "0x2000001" : "60");
	}
	
	/**
	 * @param funcname
	 * @return
	 */
	public String getExternalFunctionName(String funcname)
	{
		return (this.platform == Platform.MAC ? "_" + funcname : funcname);
	}
	
	/**
	 * @return
	 */
	public String getEntryPointLabelName()
	{
		return (this.platform == Platform.MAC ? "_main" : "_start");
	}
	
	/**
	 * @return
	 */
	public String getExitSysCallLabel()
	{
		return "exit_program#";
	}
	
	/**
	 * @param literal
	 */
	public void foundStringLiteral(String literal)
	{
		this.symbolTable.foundStringLiteral(literal);
	}
	
	/**
	 * @param literal
	 * @return
	 */
	public String getStringLiteralLabel(String literal)
	{
		return this.symbolTable.getStingLiteralLabel(literal);
	}
	
	/**
	 * @param varname
	 */
	public void declareGlobalVariable(AstGlobal gvar)
	{
		this.symbolTable.declareGlobalVariable(gvar);
	}
	
	/**
	 * @param varname
	 */
	public String getGlobalVariableLabel(String varname)
	{
		return "global_variable#" + varname;
	}
	
	/**
	 * @param identifier
	 * @return
	 */
	public SymbolType getSymbolType(String identifier)
	{
		SymbolType t = this.symbolTable.getSymbolType(identifier);	
		return t;
	}
	
	/**
	 * @param idenfier
	 * @return
	 * @throws CompileErrorException
	 */
	public ExprType getVariableType(String idenfier) throws CompileErrorException
	{
		ExprType t = this.symbolTable.getVariableType(idenfier);
		return t;
	}
	
	/**
	 * @param varname
	 * @throws IOException
	 */
	public void allocateGlobalVariables() throws IOException
	{
		this.printComment("; global variables");
		this.printSection("section .data");
		
		List<String> gvs=  symbolTable.getGlobalVariables();
		for (String gvarname: gvs) {
			this.printCode(	this.getGlobalVariableLabel(gvarname) + ": db 0, 0, 0, 0, 0, 0, 0, 0 ; allocate 64bits for each global variables. Initialize with zeros.", 1);
		}
		
		return;
	}
	
	/**
	 * @throws IOException
	 */
	public void allocateStringLiterals() throws IOException
	{
		List<StringLiteralAndLabel> list = this.symbolTable.getStringLabels();

		this.printComment("; string literals ");
		this.printSection("section .data");
		for (StringLiteralAndLabel l: list) {
			this.printCode(l.label + ": db " + l.literal + ", 0");
		}
	}
	
	/**
	 * 
	 */
	public void resetLocalVariableTable()
	{
		this.symbolTable.resetLocalVariableTable();
	}
	
	/**
	 * @throws IOException
	 */
	public void declareLocalVariable(AstLocal lvar) throws SyntaxErrorException
	{
		this.symbolTable.declareLocalVariable(lvar);
	}
	
	/**
	 * 
	 */
	public void assignLocalVariableIndices() {
		this.symbolTable.assignLocalVariableIndices();
	}
	
	/**
	 * @return
	 */
	public int getStackFrameExtensionSize() {
		return this.symbolTable.getStackFrameExtensionSize();
	}
	
	/**
	 * @param id
	 * @throws SyntaxErrorException
	 */
	public int getStackIndexOfLocalVariable(String id) throws SyntaxErrorException
	{
		int idx = this.symbolTable.getStackIndexOfLocalVariable(id);
		if (idx == 0) {
			throw new SyntaxErrorException("undeclared local variable found: " + this);
		}
		return idx;
	}
	
	/**
	 * 
	 */
	/**
	 * 
	 */
	public void preprocess() throws CompileErrorException
	{
		this.program.preprocessGlobalVariables(this);
		this.program.preprocessStringLiterals(this);
	}
	
	/**
	 * @throws IOException
	 */
	public void generateCode() throws IOException, CompileErrorException
	{
		File f = new File(filename);
		writer = new PrintWriter(f);
		
		//--------------------------------------
		//extern 
		this.printComment("; 64 bit code.");
		this.printCode	("bits 64", 0);
		
		//--------------------------------------
		//extern 
		this.printComment("; to use the printf() function.");

		this.printCode	("extern " + this.getExternalFunctionName("printf"), 0);
		this.printCode	();
		
		//--------------------------------------
		//data section
		this.printComment("; data section.");
		this.printSection("section .data");
		
		//exit message format string.
		this.printCode	(	"exit_fmt#:    db \"exit code:%d\", 10, 0 ; the format string for the exit message.");
		this.printCode	();
		
		//print format strings.
		this.printCode	(	"print_int_fmt#:    db \"%d\", 10, 0 ; the format string for the print int.");
		this.printCode	(	"print_string_fmt#:    db \"%s\", 10, 0 ; the format string for the print string.");
		this.printCode	(	"print_double_fmt#:    db \"%lf\", 10, 0 ; the format string for the print double.");
		this.printCode();
		this.printCode	(	"print_boolean_string_true#:    db \"true\", 0 ; the format string for the print double.");
		this.printCode	(	"print_boolean_string_false#:    db \"false\", 0 ; the format string for the print double.");
		this.printCode();
		
		//string literals.
		this.allocateStringLiterals();
		this.printCode();
		
		//global variables
		this.allocateGlobalVariables();
		this.printCode();
		
		//--------------------------------------
		//text section
		this.printComment("; text section");
		this.printSection("section .text");
		this.printCode	(	"global " + this.getEntryPointLabelName() + " ; the entry point.");
		this.printCode();
		
		
		//the exit_program subroutine.		
		this.printComment("; the subroutine for sys-exit. rax will be the exit code.");
		this.printLabel(getExitSysCallLabel());				// where we exit the program.
		
		this.printCode(	"and rsp, 0xFFFFFFFFFFFFFFF0 ; stack must be 16 bytes aligned to call a C function.");
		this.printCode(	"push rax ; we need to preserve rax here.");
		this.printCode();
		this.printCode(	"; call printf to print out the exti code.");
		this.printCode(	"lea rdi, [rel exit_fmt#] ; the format string");
		this.printCode(	"mov rsi, rax		; the exit code ");
		this.printCode(  "mov rax, 0			; no xmm register is used.");
		this.printCode(	"call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov rax, "+ this.getExitSysCallNum() + "; specify the exit sys call.");
		this.printCode(	"pop rdi ; this is the rax value we pushed at the entry of this sub routine");
		this.printCode(	"syscall ; exit!");
		this.printCode();
		
		//the print_int function.		
		this.printComment("; the function for print(int).");
		this.printLabel(getPrintIntLabel());
		this.printCode(	"push rbp 		; store the current base pointer.");
		this.printCode(	"mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.printCode(	"and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.printCode();
		this.printCode( "lea  rdi, [rel print_int_fmt#]");
		this.printCode( "mov  rsi, rax");
		this.printCode( "mov  rax, 0");
		this.printCode( "call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov  rsp, rbp");;
		this.printCode(	"pop  rbp");
		this.printCode( "ret");
		this.printCode();

		this.printComment("; the function for print(string).");
		this.printLabel(getPrintStringLabel());
		this.printCode(	"push rbp 		; store the current base pointer.");
		this.printCode(	"mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.printCode(	"and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.printCode();
		this.printCode( "lea  rdi, [rel print_string_fmt#]");
		this.printCode( "mov  rsi, rax");
		this.printCode( "mov  rax, 0");
		this.printCode( "call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov  rsp, rbp");;
		this.printCode(	"pop  rbp");
		this.printCode( "ret");
		this.printCode();

		this.printComment("; the function for print(double).");
		this.printLabel(getPrintDoubleLabel());
		this.printCode(	"push rbp 		; store the current base pointer.");
		this.printCode(	"mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.printCode(	"and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.printCode();
		this.printCode( "lea  rdi, [rel print_double_fmt#]");
		this.printCode( "movq xmm0, rax");
		this.printCode( "mov  rax, 1");
		this.printCode( "call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov  rsp, rbp");;
		this.printCode(	"pop  rbp");
		this.printCode( "ret");
		this.printCode();

		this.printComment("; the function for print(boolean).");
		this.printLabel(getPrintBooleanLabel());
		this.printCode(	"push rbp 		; store the current base pointer.");
		this.printCode(	"mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.printCode(	"and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.printCode();
		this.printCode(	"cmp rax, 0");
		this.printCode( "je .print_boolean_false#");
		this.printCode();
		this.printCode( ".print_boolean_true#:");
		this.printCode( "lea rsi, [rel print_boolean_string_true#]");
		this.printCode( "jmp .print_boolean_print#");
		this.printCode();
		this.printCode( ".print_boolean_false#:");
		this.printCode( "lea rsi, [rel print_boolean_string_false#]");
		this.printCode();
		
		this.printCode( ".print_boolean_print#:");
		this.printCode( "lea rdi, [rel print_string_fmt#]");
		this.printCode( "mov rax, 0");
		this.printCode( "call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov  rsp, rbp");;
		this.printCode(	"pop  rbp");
		this.printCode( "ret");
		this.printCode();

		//main function
		this.printLabel	(this.getEntryPointLabelName());
		this.printCode(	"mov rax, 0 ; initialize the accumulator register.");
		
		
		this.program.cgen(this);
		
		//epilogue
		this.printCode();
		this.printCode(	"jmp " + getExitSysCallLabel() + " ; exit the program, rax should hold the exit code.");

		writer.flush();
		writer.close();
		
		return;
	}
	
	
	/**
	 * 
	 */
	public void printCode()
	{
		writer.println("");
		return;		
	}
	
	/**
	 * @param buf
	 * @param code
	 * @param indent
	 */
	public void printCode(String code) throws IOException
	{
		this.printCode(code, 1);
		return;
	}
	
	/**
	 * @param label
	 */
	public void printLabel(String label) throws IOException
	{
		this.printCode(label + ":", 0);
	}
	
	/**
	 * @param comment
	 */
	public void printComment(String comment) throws IOException
	{
		this.printCode(comment, 0);
	}
	
	/**
	 * @param section
	 */
	public void printSection(String section) throws IOException
	{
		this.printCode(section, 0);
	}
	
	/**
	 * @param buf
	 * @param code
	 * @param indent
	 */
	public void printCode(String code, int indent) throws IOException
	{
		for (int i = 0; i < indent; i++) {
			writer.print("\t");
		}
		
		writer.println(code);
		
		return;
	}
	
	/**
	 * 
	 */
	public void printGlobalVariables() {
		this.symbolTable.printGlobalVariables();
	}

}
