package lexer;

// Lab 1, exercise 1: read the MiniJava specification
// carefully, and answer these two questions:
//   1. should we add other token kinds?
//   2. which tokens come with an extra "lexeme", and
//      which do not?
public class Token {
    // kind of the token
    public String kind;
    // extra lexeme for this token, if any
    public String lexeme;
    // on which line of the source file this token appears
    public Integer lineNum;

    public Token(String kind, Integer lineNum) {
        this.kind = kind;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        String s;

        s = ": " + ((this.lexeme == null) ? "<NONE>" : this.lexeme)
                + " : at line " + (this.lineNum == null ? "<null>" : lineNum.toString());
        return this.kind + s;
    }
}