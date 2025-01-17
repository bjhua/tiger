package lexer;

import java.util.Optional;

// Lab 1, exercise 8: read the MiniJava specification carefully,
// and fill in other possible tokens.
public class Token {
    // alphabetically ordered
    public enum Kind {
        ADD,
        CLASS,
        COMMA,
        DOT,
        EOF,
        ID,
        INT,
        LBRACKET,
        LENGTH,
        LPAREN,
        NEW,
        RBRACKET,
        RPAREN,
        SEMICOLON,
    }

    // kind of the token
    public Kind kind;
    // extra lexeme for this token, if any
    public Optional<String> lexeme;
    // position of the token in the source file: (row, column)
    public Integer rowNum;
    public Integer colNum;


    public Token(Kind kind,
                 Integer rowNum,
                 Integer colNum) {
        this.kind = kind;
        this.lexeme = Optional.empty();
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public Token(Kind kind,
                 String lexeme,
                 Integer rowNum,
                 Integer colNum) {
        this.kind = kind;
        this.lexeme = Optional.of(lexeme);
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    @Override
    public String toString() {
        String s;

        s = ": " + (this.lexeme.orElse("<NONE>")) +
                ": at row " + (this.rowNum == null ? "<null>" : rowNum.toString()) +
                ": at column " + (this.colNum == null ? "<null>" : colNum.toString());
        return this.kind + s;
    }
}