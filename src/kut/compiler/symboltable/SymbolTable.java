package kut.compiler.symboltable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kut.compiler.exception.CompileErrorException;
import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.lexer.TokenClass;
import kut.compiler.parser.ast.AstGlobal;
import kut.compiler.parser.ast.AstLocal;

public class SymbolTable 
{	
	protected Map<String, AstGlobal> 			globalVariables		;
	protected Map<String, LocalVariableInfo>	localVariables		;
	
	protected Map<String, String>	stringLiteralLabels	;
	protected int 					intStringLiteralIndex;
	

	/**
	 * 
	 */
	public SymbolTable() {
		globalVariables = new HashMap<String, AstGlobal>();
		stringLiteralLabels  = new HashMap<String, String>();
		intStringLiteralIndex = 0;
	}
	
	
	/**
	 * @param idenfier
	 * @return
	 */
	public ExprType getVariableType(String idenfier) throws CompileErrorException
	{
		int t = TokenClass.ERROR;
		if (localVariables.containsKey(idenfier)) {
			LocalVariableInfo info = localVariables.get(idenfier);
			t = info.node.getTypeToken().getC();
		}
		else if (globalVariables.containsKey(idenfier)) {
			t = globalVariables.get(idenfier).getTypeToken().getC();
		}

		switch(t) {
		case TokenClass.INT:
			return ExprType.INT;
		
		case TokenClass.STRING:
			return ExprType.STRING;
			
		case TokenClass.DOUBLE:
			return ExprType.DOUBLE;
			
		case TokenClass.BOOLEAN:
			return ExprType.BOOLEAN;

		default:
			break;
		}

		throw new CompileErrorException("an error occured during type-checking (unknown identifier). the code shouldn't reach here.");
	}
	
	/**
	 * @param label
	 */
	public void foundStringLiteral(String literal) 
	{
		
		if (this.getStingLiteralLabel(literal) != null) {
			return;
		}
		
		String label = "string_literal#" + intStringLiteralIndex;
		intStringLiteralIndex++;
		this.stringLiteralLabels.put(literal, label);
		return;
	}
	
	
	/**
	 * @param string
	 * @return
	 */
	public String getStingLiteralLabel(String literal) 
	{
		if (this.stringLiteralLabels.containsKey(literal) == false) {
			return null;
		}
		return this.stringLiteralLabels.get(literal);
	}
	
	/**
	 * @return
	 */
	public List<StringLiteralAndLabel> getStringLabels(){
		LinkedList<StringLiteralAndLabel> ls = new LinkedList<StringLiteralAndLabel>();
		for (String k: this.stringLiteralLabels.keySet()) {
			StringLiteralAndLabel l = new StringLiteralAndLabel();
			l.literal 	= k;
			l.label		= this.getStingLiteralLabel(k);
			ls.add(l);
		}
		return ls;
	}
	
	/**
	 * @param varname
	 * @throws SyntaxErrorException
	 */
	public void declareGlobalVariable(AstGlobal gvar) 
	{
		String varname = gvar.getVarName().getIdentifier();
		globalVariables.put(varname, gvar);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public SymbolType getSymbolType(String id)
	{
		if (localVariables.containsKey(id)) {
			return  SymbolType.LocalVariable;
		}

		if (globalVariables.containsKey(id)) {
			return SymbolType.GlobalVariable; 
		}
		
		return SymbolType.Unknown;
	}
	
	
	/**
	 * @return
	 */
	public List<String> getGlobalVariables()
	{
		return new LinkedList<String>(globalVariables.keySet());
	}
	

	/**
	 * 
	 */
	public void printGlobalVariables() 
	{	
		System.out.println("the list of global variables");
		for (String id: globalVariables.keySet()) {
			System.out.println(globalVariables.get(id));
		}
	}
	
	/**
	 * @param t
	 */
	public void declareLocalVariable(AstLocal lvar) throws SyntaxErrorException
	{
		String id = lvar.getVarName().getIdentifier();
		if (localVariables.containsKey(id)){
			throw new SyntaxErrorException("duplicate local variable declarations : " + lvar.getVarName());
		}
		
		LocalVariableInfo i = new LocalVariableInfo();
		i.node = lvar;
		i.stackIndex = 0;
		
		this.localVariables.put(id, i);
		return;
	}
	
	/**
	 * 
	 */
	public void resetLocalVariableTable() {
		this.localVariables = new HashMap<String, LocalVariableInfo>();
	}
	
	
	
	/**
	 * @param vname
	 * @return
	 */
	public int getStackIndexOfLocalVariable(String vname)
	{
		if (!this.localVariables.containsKey(vname)) {
			return 0;
		}
		LocalVariableInfo info = this.localVariables.get(vname);
		
		return info.stackIndex;
	}
	
	/**
	 * @return
	 */
	public int getStackFrameExtensionSize()
	{
		int min = 0;
		for (LocalVariableInfo s: this.localVariables.values()) {
			min = min > s.stackIndex ? s.stackIndex : min;
		}
				
		return -min;
	}
	

	/**
	 * 
	 */
	public void assignLocalVariableIndices() {
		int idx = -8; // the previous rbp is located at rbp + 0, so got to start from -8.
		for (LocalVariableInfo s: this.localVariables.values()) {
			s.stackIndex = idx;
			idx -= 8;
		}
	}
}
