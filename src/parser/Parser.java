package parser;

import lexer.Lexer;
import lexer.Token;

public class Parser {
    Lexer lexer;
    Token current;

    public Parser(String fname, java.io.InputStream fstream) {
        lexer = new Lexer(fname, fstream);
        current = lexer.nextToken();
    }

    // /////////////////////////////////////////////
    // utility methods to connect the lexer
    // and the parser.

    private void advance() { current = lexer.nextToken(); }

    private void eatToken(String kind) {
        if (kind.equals(current.kind)) {
            advance();
            return;
        }
        System.out.println("Expects: " + kind);
        System.out.println("But got: " + current.kind);
        System.exit(1);
    }

    private void error() {
        System.out.println("Syntax error: compilation aborting...\n");
        System.exit(1);
        return;
    }

    // ////////////////////////////////////////////////////////////
    // below are method for parsing.

    // A bunch of parsing methods to parse expressions. The messy
    // parts are to deal with precedence and associativity.

    // ExpList -> Exp ExpRest*
    // ->
    // ExpRest -> , Exp
    private void parseExpList() {
        if (current.kind.equals(")"))
            return;
        parseExp();
        while (current.kind.equals(",")) {
            advance();
            parseExp();
        }
        return;
    }

    // AtomExp -> (exp)
    // -> INTEGER_LITERAL
    // -> true
    // -> false
    // -> this
    // -> id
    // -> new int [exp]
    // -> new id ()
    private void parseAtomExp() {
        switch (current.kind) {
        case "(":
            advance();
            parseExp();
            eatToken(")");
            return;
        case "Num", "this", "Id", "true":
            advance();
            return;
        case "new": {
            advance();
            switch (current.kind) {
            case "int":
                advance();
                eatToken("[");
                parseExp();
                eatToken("]");
                return;
            case "Id":
                advance();
                eatToken("(");
                eatToken(")");
                return;
            default:
                error();
                return;
            }
        }
        default:
            error();
            return;
        }
    }

    // NotExp -> AtomExp
    // -> AtomExp .id (expList)
    // -> AtomExp [exp]
    // -> AtomExp .length
    private void parseNotExp() {
        parseAtomExp();
        while (current.kind.equals(".") ||
               current.kind.equals("[")) {
            if (current.kind.equals(".")) {
                advance();
                if (current.kind.equals("length")) {
                    advance();
                    return;
                }
                eatToken("Id");
                eatToken("(");
                parseExpList();
                eatToken(")");
            } else {
                advance();
                parseExp();
                eatToken("]");
            }
        }
        return;
    }

    // TimesExp -> ! TimesExp
    // -> NotExp
    private void parseTimesExp() {
        while (current.kind.equals("!")) {
            advance();
        }
        parseNotExp();
        return;
    }

    // AddSubExp -> TimesExp * TimesExp
    // -> TimesExp
    private void parseAddSubExp() {
        parseTimesExp();
        while (current.kind.equals("*")) {
            advance();
            parseTimesExp();
        }
        return;
    }

    // LtExp -> AddSubExp + AddSubExp
    // -> AddSubExp - AddSubExp
    // -> AddSubExp
    private void parseLtExp() {
        parseAddSubExp();
        while (current.kind.equals("+") ||
               current.kind.equals("-")) {
            advance();
            parseAddSubExp();
        }
        return;
    }

    // AndExp -> LtExp < LtExp
    // -> LtExp
    private void parseAndExp() {
        parseLtExp();
        while (current.kind.equals("<")) {
            advance();
            parseLtExp();
        }
        return;
    }

    // Exp -> AndExp && AndExp
    // -> AndExp
    private void parseExp() {
        parseAndExp();
        while (current.kind.equals("&&")) {
            advance();
            parseAndExp();
        }
        return;
    }

    // Statement -> { Statement* }
    // -> if ( Exp ) Statement else Statement
    // -> while ( Exp ) Statement
    // -> System.out.println ( Exp ) ;
    // -> id = Exp ;
    // -> id [ Exp ]= Exp ;
    private void parseStatement() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a statement.
        new util.Todo();
    }

    // Statements -> Statement Statements
    // ->
    private void parseStatements() {
        while (current.kind.equals("{") ||
               current.kind.equals("if") ||
               current.kind.equals("while") ||
               current.kind.equals("System") ||
               current.kind.equals("Id")) {
            parseStatement();
        }
        return;
    }

    // Type -> int []
    // -> boolean
    // -> int
    // -> id
    private void parseType() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a type.
        new util.Todo();
    }

    // VarDecl -> Type id ;
    private void parseVarDecl() {
        // to parse the "Type" nonterminal in this method, instead of writing
        // a fresh one.
        parseType();
        eatToken("Id");
        eatToken(";");
        return;
    }

    // VarDecls -> VarDecl VarDecls
    // ->
    private void parseVarDecls() {
        while (current.kind.equals("int") ||
               current.kind.equals("boolean") ||
               current.kind.equals("Id")) {
            parseVarDecl();
        }
        //        return;
    }

    // FormalList -> Type id FormalRest*
    // ->
    // FormalRest -> , Type id
    private void parseFormalList() {
        if (current.kind.equals("int") ||
            current.kind.equals("boolean") ||
            current.kind.equals("Id")) {
            parseType();
            eatToken("Id");
            while (current.kind.equals(",")) {
                advance();
                parseType();
                eatToken("Id");
            }
        }
        return;
    }

    // Method -> public Type id ( FormalList )
    // { VarDecl* Statement* return Exp ;}
    private void parseMethod() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a method.
        new util.Todo();
        return;
    }

    // MethodDecls -> MethodDecl MethodDecls
    // ->
    private void parseMethodDecls() {
        while (current.kind.equals("public")) {
            parseMethod();
        }
        return;
    }

    // ClassDecl -> class id { VarDecl* MethodDecl* }
    // -> class id extends id { VarDecl* MethodDecl* }
    private void parseClassDecl() {
        eatToken("class");
        eatToken("Id");
        if (current.kind.equals("extends")) {
            eatToken("extens");
            eatToken("Id");
        }
        eatToken("{");
        parseVarDecls();
        parseMethodDecls();
        eatToken("}");
        return;
    }

    // ClassDecls -> ClassDecl ClassDecls
    // ->
    private void parseClassDecls() {
        while (current.kind.equals("class")) {
            parseClassDecl();
        }
        return;
    }

    // MainClass -> class id
    // {
    // public static void main ( String [] id )
    // {
    // Statement
    // }
    // }
    private void parseMainClass() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a main class as described by the
        // grammar above.
        new util.Todo();
    }

    // Program -> MainClass ClassDecl*
    private void parseProgram() {
        parseMainClass();
        parseClassDecls();
        eatToken("EOF");
        return;
    }

    public void parse() {
        parseProgram();
        return;
    }
}
