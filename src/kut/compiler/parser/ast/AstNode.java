package kut.compiler.parser.ast;



import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.symboltable.ExprType;

/**
 * @author hnishino
 *
 */
public abstract class AstNode {
	
	
	
		/**
		 * @return the generated code string
		 */
		public abstract void cgen(CodeGenerator gen) throws IOException, CompileErrorException;
		

		
		/**
		 * @param gen
		 * @throws CompileErrorException
		 */
		public ExprType checkTypes(CodeGenerator gen) throws CompileErrorException
		{
			throw new CompileErrorException("this node should not be type-checked. a bug.");
		}
			
		
		/**
		 * @param gen
		 */
		public void preprocessGlobalVariables(CodeGenerator gen) {
			return;
		}
		
		/**
		 * @param gen
		 */
		public void preprocessStringLiterals(CodeGenerator gen) {
			return;
		}
		
		/**
		 * @param gen
		 */
		public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException 
		{
			return;
		}
		
		/**
		 * 
		 */
		public void printTree() {
			this.printTree(0);
		}
		
		protected abstract void printTree(int indent);
		
		/**
		 * @param indent
		 */
		protected void println(int indent, String s) {
			this.printIndent(indent);
			System.out.println(s);
		}
		
		/**
		 * @param indent
		 */
		protected void printIndent(int indent) {
			for (int i = 0; i < indent; i++) {
				System.out.print("    ");
			}
		}

}
