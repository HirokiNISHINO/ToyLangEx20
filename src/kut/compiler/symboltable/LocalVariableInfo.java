package kut.compiler.symboltable;

import kut.compiler.parser.ast.AstLocal;

public class LocalVariableInfo {
	AstLocal	node;
	int			stackIndex;		
	
	public static final int LOCAL_VAR_INDEX_NOT_ASIGNED = 0;
	public static final int FUN_PARM_INDEX_NOT_ASIGNED = -1;
	
}

