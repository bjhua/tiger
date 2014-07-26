import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import control.CommandLine;
import control.Control;

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

    // /////////////////////////////////////////////
    // the straight-line interpreter (and compiler)    
    switch (Control.ConSlp.action){
    case NONE:
      System.exit(0);
      break;
    default:
      slp.Main slpmain = new slp.Main();
      if (Control.ConSlp.div) {
        slpmain.doit(slp.Samples.dividebyzero);
        System.exit(0);
      }
      slpmain.doit(slp.Samples.prog);
      System.exit(0);
    }

    
    if (fname == null) {
      cmd.usage();
      return;
    }

    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (Control.ConLexer.test) {
      System.out.println("Testing the lexer. All tokens:");
      try {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();
        while (token.kind != Token.Kind.TOKEN_EOF) {
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
    try {
      fstream = new BufferedInputStream(new FileInputStream(fname));
      parser = new Parser(fname, fstream);

      parser.parse();

      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return;
  }
}
