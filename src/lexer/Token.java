package lexer;

public class Token {
    // Lab 1, exercise 1: read the MiniJava specification
    // carefully, and answer these two questions:
    //   1. should we add other token kinds?
    //   2. which tokens come with an extra "lexeme", and
    //      which don't?
    // It is highly recommended that these token names are
    // alphabetically ordered, if you add new ones.

    public String kind;     // kind of the token
    public String lexeme; // extra lexeme for this token, if any
    // on which line of the source file this token appears
    public Integer lineNum;

    // Some tokens don't come with lexeme but
    // others do.
    public Token(String kind, Integer lineNum) {
        this.kind = kind;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        String s;

        // to check that the "lineNum" field has been properly set.
        if (this.lineNum == null)
            new util.Todo();

        s = ": " + ((this.lexeme == null) ? "<NONE>" : this.lexeme) +
            " : at line " + this.lineNum.toString();
        return this.kind.toString() + s;
    }
}