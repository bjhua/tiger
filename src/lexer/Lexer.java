package lexer;

import util.Todo;

import java.io.InputStream;

import static control.Control.Lexer.dumpToken;

public class Lexer {
    // the input file name to be compiled
    String fileName;
    // input stream for the above file
    InputStream fileStream;

    public Lexer(String fileName, InputStream fileStream) {
        this.fileName = fileName;
        this.fileStream = fileStream;
    }

    // When called, return the next token (refer to the code "Token.java")
    // from the input stream.
    // Return TOKEN_EOF when reaching the end of the input stream.
    private Token nextToken0() throws Exception {
        int c = this.fileStream.read();
        if (-1 == c)
            // The value for "lineNum" is now "null",
            // you should modify this to an appropriate
            // line number for the "EOF" token.
            return new Token(Token.Kind.EOF, null, null);

        // skip all kinds of "blanks"
        while (' ' == c || '\t' == c || '\n' == c) {
            c = this.fileStream.read();
        }
        if (-1 == c)
            return new Token(Token.Kind.EOF, null, null);

        if ('+' == c) {
            return new Token(Token.Kind.ADD, null, null);
        }
        if (',' == c) {
            return new Token(Token.Kind.COMMA, null, null);
        }
        // Lab 1, exercise 9: supply missing code to
        // recognize other kind of tokens.
        // Hint: think carefully about the basic
        // data structure and algorithms. The code
        // is not that much and may be less than 50 lines. If you
        // find you are writing a lot of code, you
        // are on the wrong way.
        throw new Todo();
    }

    public Token nextToken() {
        Token t = null;

        try {
            t = this.nextToken0();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (dumpToken) {
            System.out.println(t);
        }
        return t;
    }
}
