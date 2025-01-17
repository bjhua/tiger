package parser;

import lexer.Lexer;
import lexer.Token;
import util.Todo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import static java.lang.System.exit;

public class Parser {
    String inputFileName;
    BufferedInputStream inputStream;
    Lexer lexer;
    Token current;

    public Parser(String fileName) {
        this.inputFileName = fileName;
    }

    // /////////////////////////////////////////////
    // utility methods to connect the lexer and the parser.
    private void advance() {
        current = lexer.nextToken();
    }

    private void eatToken(Token.Kind kind) {
        if (kind.equals(current.kind)) {
            advance();
            return;
        }
        System.out.println("Expects: " + kind);
        System.out.println("But got: " + current.kind);
        error("syntax error");
    }

    private void error(String errMsg) {
        System.out.println("Error: " + errMsg + ", compilation aborting...\n");
        exit(1);
    }

    // ////////////////////////////////////////////////////////////
    // The followings are methods for parsing.

    // A bunch of parsing methods to parse expressions.
    // The messy parts are to deal with precedence and associativity.

    // ExpList -> Exp ExpRest*
    // ->
    // ExpRest -> , Exp
    private void parseExpList() {
        if (current.kind.equals(Token.Kind.RPAREN))
            return;
        parseExp();
        while (current.kind.equals(Token.Kind.COMMA)) {
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
            case LPAREN:
                advance();
                parseExp();
                eatToken(Token.Kind.RPAREN);
                return;
            case ID:
                advance();
                return;
            case NEW: {
                advance();
                switch (current.kind) {
                    case INT:
                        advance();
                        eatToken(Token.Kind.LBRACKET);
                        parseExp();
                        eatToken(Token.Kind.RBRACKET);
                        return;
                    case ID:
                        advance();
                        eatToken(Token.Kind.LPAREN);
                        eatToken(Token.Kind.RPAREN);
                        return;
                    default:
                        throw new Todo();
                }
            }
            default:
                throw new Todo();
        }
    }

    // NotExp -> AtomExp
    // -> AtomExp .id (expList)
    // -> AtomExp [exp]
    // -> AtomExp .length
    private void parseNotExp() {
        parseAtomExp();
        while (current.kind.equals(Token.Kind.DOT) ||
                current.kind.equals(Token.Kind.LBRACKET)) {
            if (current.kind.equals(Token.Kind.DOT)) {
                advance();
                if (current.kind.equals(Token.Kind.LENGTH)) {
                    advance();
                    return;
                }
                eatToken(Token.Kind.ID);
                eatToken(Token.Kind.LPAREN);
                parseExpList();
                eatToken(Token.Kind.RPAREN);
            } else {
                advance();
                parseExp();
                eatToken(Token.Kind.RBRACKET);
            }
        }
        return;
    }

    // TimesExp -> ! TimesExp
    // -> NotExp
    private void parseTimesExp() {
        throw new Todo();

    }

    // AddSubExp -> TimesExp * TimesExp
    // -> TimesExp
    private void parseAddSubExp() {
        parseTimesExp();
        throw new Todo();
    }

    // LtExp -> AddSubExp + AddSubExp
    // -> AddSubExp - AddSubExp
    // -> AddSubExp
    private void parseLtExp() {
        parseAddSubExp();
        throw new Todo();
    }

    // AndExp -> LtExp < LtExp
    // -> LtExp
    private void parseAndExp() {
        parseLtExp();
        throw new Todo();
    }

    // Exp -> AndExp && AndExp
    // -> AndExp
    private void parseExp() {
        parseAndExp();
        throw new Todo();
    }

    // Statement -> { Statement* }
    // -> if ( Exp ) Statement else Statement
    // -> while ( Exp ) Statement
    // -> System.out.println ( Exp ) ;
    // -> id = Exp ;
    // -> id [ Exp ]= Exp ;
    private void parseStatement() {
        // to parse a statement.
        throw new Todo();
    }

    // Statements -> Statement Statements
    // ->
    private void parseStatements() {
        throw new Todo();
    }

    // Type -> int []
    // -> boolean
    // -> int
    // -> id
    private void parseType() {
        // to parse a type.
        throw new Todo();
    }

    // VarDecl -> Type id ;
    private void parseVarDecl() throws Exception {
        // to parse the "Type" non-terminal in this method,
        // instead of writing a fresh one.
        parseType();
        eatToken(Token.Kind.ID);
        eatToken(Token.Kind.SEMICOLON);
        return;
    }

    // VarDecls -> VarDecl VarDecls
    // ->
    private void parseVarDecls() {
        throw new util.Todo();
        //        return;
    }

    // FormalList -> Type id FormalRest*
    // ->
    // FormalRest -> , Type id
    private void parseFormalList() {
        throw new Todo();
    }

    // Method -> public Type id ( FormalList )
    // { VarDecl* Statement* return Exp ;}
    private void parseMethod() {
        // to parse a method.
        throw new Todo();
    }

    // MethodDecls -> MethodDecl MethodDecls
    // ->
    private void parseMethodDecls() {
        throw new util.Todo();
    }

    // ClassDecl -> class id { VarDecl* MethodDecl* }
    // -> class id extends id { VarDecl* MethodDecl* }
    private void parseClassDecl() {
        eatToken(Token.Kind.CLASS);
        eatToken(Token.Kind.ID);
        throw new util.Todo();
    }

    // ClassDecls -> ClassDecl ClassDecls
    // ->
    private void parseClassDecls() {
        while (current.kind.equals(Token.Kind.CLASS)) {
            parseClassDecl();
        }
        return;
    }

    // MainClass -> class id {
    //   public static void main ( String [] id ) {
    //     Statement
    //   }
    // }
    private void parseMainClass() {
        // Lab 1. Exercise 11: Fill in the missing code
        // to parse a main class as described by the
        // grammar above.
        throw new Todo();
    }

    // Program -> MainClass ClassDecl*
    private void parseProgram() {
        parseMainClass();
        parseClassDecls();
        eatToken(Token.Kind.EOF);
        return;
    }

    private void initParser() {
        try {
            this.inputStream = new BufferedInputStream(new FileInputStream(this.inputFileName));
        } catch (Exception e) {
            error("unable to open file" + this.inputFileName);
        }

        this.lexer = new Lexer(this.inputFileName, this.inputStream);
        this.current = lexer.nextToken();
    }

    private void finalizeParser() {
        try {
            this.inputStream.close();
        } catch (Exception e) {
            error("unable to close file");
        }
    }

    public Object parse() {
        initParser();
        parseProgram();
        finalizeParser();
        return null;
    }
}
