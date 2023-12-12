import control.CommandLine;
import control.Control;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import lexer.Lexer;
import lexer.Token;
import parser.Parser;


// the straight-line interpreter (and compiler)
public class Tiger {
    public static void main(String[] args) {
        InputStream fstream;
        Parser parser;

        // ///////////////////////////////////////////////////////
        // handle command line arguments
        CommandLine cmd = new CommandLine();
        String fname = cmd.scan(args);

        if (Control.ConLexer.test) {
            System.out.println("Testing the lexer. All tokens:");
            try {
                fstream = new BufferedInputStream(new FileInputStream(fname));
                Lexer lexer = new Lexer(fname, fstream);
                Token token = lexer.nextToken();
                while (!token.kind.equals("EOF")) {
                    System.out.println(token.toString());
                    token = lexer.nextToken();
                }
                fstream.close();
            } catch (Exception e) {
                System.out.printf(e.toString());
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
            System.out.printf(e.toString());
            System.exit(1);
        }
        return;
    }
}
