package kut.compiler;


import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.compiler.Platform;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Lexer;
import kut.compiler.parser.Parser;
import kut.compiler.parser.ast.AstNode;

public class ToyLang {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws CompileErrorException, IOException
	{
		if (args.length != 2) {
			System.out.println("usage: java MyLang source_code_filename output_filename");
			return;
		}
		
		boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
		
		Lexer lexer = new Lexer(args[0]);
		
		System.out.println("parsing...");
		Parser parser = new Parser(lexer);
		AstNode program = parser.parse();
		System.out.println("done.");
		
		program.printTree();
		
		
		System.out.println("compiling...");
		
		CodeGenerator generator = new CodeGenerator(program, args[1],  isMac ? Platform.MAC : Platform.LINUX);
		generator.preprocess();
		generator.printGlobalVariables();

		generator.generateCode();
		System.out.println("done.");
		

	}

}
