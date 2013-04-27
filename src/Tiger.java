import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

import control.CommandLine;

import parser.Parser;

public class Tiger
{
  public static void main(String[] args)
  {
    InputStream fstream;
    Parser parser;

    // ///////////////////////////////////////////////////////
    // handle command line arguments
    CommandLine cmd = new CommandLine();
    String fname = cmd.scan(args);

    // /////////////////////////////////////////////////////
    // to test the pretty printer on the "test/Fac.java" program
    if (control.Control.testFac) {
      System.out.println("Testing the Tiger compiler on Fac.java starting:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      ast.Fac.prog.accept(pp);
      System.out.println("Testing the Tiger compiler on Fac.java finished.");
      System.exit(1);
    }

    if (fname == null) {
      cmd.usage();
      return;
    }

    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (control.Control.testlexer) {
      System.out.println("Testing the lexer. All tokens:");
      try {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();
        while (token.kind != Kind.TOKEN_EOF) {
          System.out.println(token.toString());
          token = lexer.nextToken();
        }
        fstream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.exit(1);
    }

    // /////////////////////////////////////////////////////////
    // normal compilation phases.
    ast.program.T theAst = null;

    // parsing the file, get an AST.
    try {
      fstream = new BufferedInputStream(new FileInputStream(fname));
      parser = new Parser(fname, fstream);

      theAst = parser.parse();

      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    // pretty printing the AST, if necessary
    if (control.Control.dumpAst) {
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      theAst.accept(pp);
    }
    
    // elaborate the AST, report all possible errors.
    elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
    theAst.accept(elab);
    
    return;
  }
}
