package lexer;

import util.Todo;

import java.io.InputStream;

import static control.Control.Lexer.shouldDumpToken;

public record Lexer(String fileName,
                    InputStream fileStream) {

    // When called, return the next token (refer to the code "Token.java")
    // from the input stream.
    // Return TOKEN_EOF when reaching the end of the input stream.
    private Token nextToken0() throws Exception {
        int c = this.fileStream.read();
        // skip all kinds of "blanks"
        // think carefully about how to set up "colNum" and "rowNum" correctly?
        while (' ' == c || '\t' == c || '\n' == c) {
            c = this.fileStream.read();
        }
        switch (c) {
            case -1 -> {
                // The value for "lineNum" is now "null",
                // you should modify this to an appropriate
                // line number for the "EOF" token.
                return new Token(Token.Kind.EOF, null, null);
            }
            case '+' -> {
                return new Token(Token.Kind.ADD, null, null);
            }
            case ',' -> {
                return new Token(Token.Kind.COMMA, null, null);
            }
            default -> {
                // Lab 1, exercise 9: supply missing code to
                // recognize other kind of tokens.
                // Hint: think carefully about the basic
                // data structure and algorithms. The code
                // is not that much and may be less than 50 lines.
                // If you find you are writing a lot of code, you
                // are on the wrong way.
                throw new Todo(c);
            }
        }
    }

    public Token nextToken() {
        Token t = null;

        try {
            t = this.nextToken0();
        } catch (Exception e) {
            //e.printStackTrace();
            System.exit(1);
        }
        if (shouldDumpToken) {
            System.out.println(t);
        }
        return t;
    }
}
