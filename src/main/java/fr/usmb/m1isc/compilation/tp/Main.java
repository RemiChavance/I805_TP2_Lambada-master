package fr.usmb.m1isc.compilation.tp;

import java_cup.runtime.Symbol;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception  {
		LexicalAnalyzer yy;
		if (args.length > 0)
			yy = new LexicalAnalyzer(new FileReader(args[0])) ;
		else
			yy = new LexicalAnalyzer(new InputStreamReader(System.in)) ;
		@SuppressWarnings("deprecation")

		parser p = new parser (yy);
		Symbol s = p.parse( );

		Arbre arbre = (Arbre) s.value;
		System.out.println(arbre.toString());

		String code = arbre.generateAllCode();
		System.out.println(code);

		try {
			FileWriter myWriter = new FileWriter("TP_INFO805_Lambada.asm");
			myWriter.write(code);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Could not write into file");
			e.printStackTrace();
		}
    }

}
