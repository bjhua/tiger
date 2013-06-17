package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	public enum Nonterminal {
		PROGRAM, MAINCLASS, CLASSDECL, VARDECL, METHODDECL, FORMALLIST, FORMALREST, TYPE, STATEMENT, EXP, EXPLIST, EXPREST, ANDEXP, LTEXP, ADDSUBEXP, TIMESEXP, NOTEXP, ATOMEXP,
	}

	Kind[][] synch = new Kind[][] {
			{ Kind.TOKEN_EOF, Kind.TOKEN_CLASS }, // Program
			{ Kind.TOKEN_EOF, Kind.TOKEN_CLASS }, // MainClass
			{ Kind.TOKEN_EOF, Kind.TOKEN_CLASS }, // ClassDecl
			{ Kind.TOKEN_LBRACE, Kind.TOKEN_RBRACE, Kind.TOKEN_IF,
					Kind.TOKEN_WHILE, Kind.TOKEN_SYSTEM, Kind.TOKEN_ID,
					Kind.TOKEN_RETURN, Kind.TOKEN_INT, Kind.TOKEN_BOOLEAN,
					Kind.TOKEN_ID }, // VarDecl
			{ Kind.TOKEN_RBRACE, Kind.TOKEN_PUBLIC }, // MethodDecl
			{ Kind.TOKEN_RPAREN, Kind.TOKEN_INT, Kind.TOKEN_BOOLEAN,
					Kind.TOKEN_ID }, // FormalList
			{ Kind.TOKEN_RPAREN, Kind.TOKEN_COMMER }, // FormalRest
			{ Kind.TOKEN_ID, Kind.TOKEN_INT, Kind.TOKEN_BOOLEAN }, // Type
			{ Kind.TOKEN_RBRACE, Kind.TOKEN_RETURN, Kind.TOKEN_ELSE,
					Kind.TOKEN_LBRACE, Kind.TOKEN_IF, Kind.TOKEN_WHILE,
					Kind.TOKEN_SYSTEM, Kind.TOKEN_ID }, // Statement
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_LPAREN, Kind.TOKEN_NUM, Kind.TOKEN_TRUE,
					Kind.TOKEN_FALSE, Kind.TOKEN_THIS, Kind.TOKEN_ID,
					Kind.TOKEN_NEW, Kind.TOKEN_NOT }, // Exp
			{ Kind.TOKEN_RPAREN, Kind.TOKEN_LPAREN, Kind.TOKEN_NUM,
					Kind.TOKEN_TRUE, Kind.TOKEN_FALSE, Kind.TOKEN_THIS,
					Kind.TOKEN_ID, Kind.TOKEN_NEW, Kind.TOKEN_NOT }, // ExpList
			{ Kind.TOKEN_RPAREN, Kind.TOKEN_COMMER }, // ExpRest
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LPAREN, Kind.TOKEN_NUM,
					Kind.TOKEN_TRUE, Kind.TOKEN_FALSE, Kind.TOKEN_THIS,
					Kind.TOKEN_ID, Kind.TOKEN_NEW, Kind.TOKEN_NOT }, // AndExp
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LT, Kind.TOKEN_LPAREN,
					Kind.TOKEN_NUM, Kind.TOKEN_TRUE, Kind.TOKEN_FALSE,
					Kind.TOKEN_THIS, Kind.TOKEN_ID, Kind.TOKEN_NEW,
					Kind.TOKEN_NOT }, // LtExp
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LT, Kind.TOKEN_ADD,
					Kind.TOKEN_SUB, Kind.TOKEN_LPAREN, Kind.TOKEN_NUM,
					Kind.TOKEN_TRUE, Kind.TOKEN_FALSE, Kind.TOKEN_THIS,
					Kind.TOKEN_ID, Kind.TOKEN_NEW, Kind.TOKEN_NOT }, // AddSubExp
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LT, Kind.TOKEN_ADD,
					Kind.TOKEN_SUB, Kind.TOKEN_TIMES, Kind.TOKEN_LPAREN,
					Kind.TOKEN_NUM, Kind.TOKEN_TRUE, Kind.TOKEN_FALSE,
					Kind.TOKEN_THIS, Kind.TOKEN_ID, Kind.TOKEN_NEW,
					Kind.TOKEN_NOT }, // TimesExp
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LT, Kind.TOKEN_ADD,
					Kind.TOKEN_SUB, Kind.TOKEN_TIMES, Kind.TOKEN_LPAREN,
					Kind.TOKEN_NUM, Kind.TOKEN_TRUE, Kind.TOKEN_FALSE,
					Kind.TOKEN_THIS, Kind.TOKEN_ID, Kind.TOKEN_NEW }, // NotExp
			{ Kind.TOKEN_SEMI, Kind.TOKEN_RPAREN, Kind.TOKEN_RBRACK,
					Kind.TOKEN_AND, Kind.TOKEN_LT, Kind.TOKEN_ADD,
					Kind.TOKEN_SUB, Kind.TOKEN_TIMES, Kind.TOKEN_DOT,
					Kind.TOKEN_LBRACK, Kind.TOKEN_LPAREN, Kind.TOKEN_NUM,
					Kind.TOKEN_TRUE, Kind.TOKEN_FALSE, Kind.TOKEN_THIS,
					Kind.TOKEN_ID, Kind.TOKEN_NEW }, // AtomExp
	};

	Lexer lexer;
	Token current;
	List<Token> preList;
	Stack<Nonterminal> production;
	List<String> errors;

	public Parser(String fname, java.io.InputStream fstream) {
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
		preList = new ArrayList<Token>();
		production = new Stack<Parser.Nonterminal>();
		errors = new ArrayList<String>();
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		current = lexer.nextToken();
	}

	private void eatToken(Kind kind) {
		if (kind != current.kind) {
			errors.add(lexer.getFileName() + ":" + current.lineNum
					+ ":Expects: " + kind.toString() + " But got: "
					+ current.kind.toString());
			// System.exit(1);
		}
		advance();
		return;
	}

	private void skipTo(List<Kind> synch) {
		do {
			advance();
		} while (synch.contains(current.kind) != true);
		return;
	}

	private void error(String error) {
		errors.add(error);
		// System.exit(1);
		skipTo(Arrays.asList(synch[production.peek().ordinal()]));
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
		production.push(Nonterminal.EXPLIST);
		if (current.kind == Kind.TOKEN_RPAREN) {
			production.pop();
			return;
		}
		parseExp();
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			parseExp();
		}
		production.pop();
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
		production.push(Nonterminal.ATOMEXP);
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			break;
		case TOKEN_NUM:
			advance();
			break;
		case TOKEN_TRUE:
			advance();
			break;
		case TOKEN_FALSE:
			advance();
			break;
		case TOKEN_THIS:
			advance();
			break;
		case TOKEN_ID:
			advance();
			break;
		case TOKEN_NEW:
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				break;
			case TOKEN_ID:
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				break;
			default:
				error(lexer.getFileName() + ":" + current.lineNum
						+ ":Expected int or identifier!");
				break;
			}
			break;
		default:
			error(lexer.getFileName() + ":" + current.lineNum
					+ ":Expected left-paren, interger-literal, true, false,"
					+ " this, id or new!");
			break;
		}
		production.pop();
		return;
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private void parseNotExp() {
		production.push(Nonterminal.NOTEXP);
		parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT
				|| current.kind == Kind.TOKEN_LBRACK) {
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					advance();
					break;
				}
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				parseExpList();
				eatToken(Kind.TOKEN_RPAREN);
			} else {
				advance();
				parseExp();
				eatToken(Kind.TOKEN_RBRACK);
			}
		}
		production.pop();
		return;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private void parseTimesExp() {
		production.push(Nonterminal.TIMESEXP);
		while (current.kind == Kind.TOKEN_NOT) {
			advance();
		}
		parseNotExp();
		production.pop();
		return;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private void parseAddSubExp() {
		production.push(Nonterminal.ADDSUBEXP);
		parseTimesExp();
		while (current.kind == Kind.TOKEN_TIMES) {
			advance();
			parseTimesExp();
		}
		production.pop();
		return;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private void parseLtExp() {
		production.push(Nonterminal.LTEXP);
		parseAddSubExp();
		while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
			advance();
			parseAddSubExp();
		}
		production.pop();
		return;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private void parseAndExp() {
		production.push(Nonterminal.ANDEXP);
		parseLtExp();
		while (current.kind == Kind.TOKEN_LT) {
			advance();
			parseLtExp();
		}
		production.pop();
		return;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private void parseExp() {
		production.push(Nonterminal.EXP);
		parseAndExp();
		while (current.kind == Kind.TOKEN_AND) {
			advance();
			parseAndExp();
		}
		production.pop();
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
		production.push(Nonterminal.STATEMENT);
		switch (current.kind) {
		case TOKEN_LBRACE:
			advance();
			while (current.kind != Kind.TOKEN_RBRACE) {
				parseStatements();
			}
			advance();
			break;
		case TOKEN_IF:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			parseStatements();
			eatToken(Kind.TOKEN_ELSE);
			parseStatements();
			break;
		case TOKEN_WHILE:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			parseStatements();
			break;
		case TOKEN_SYSTEM:
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);
			parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			eatToken(Kind.TOKEN_SEMI);
			break;
		case TOKEN_ID:
			if (!preList.isEmpty()) {
				current = preList.get(1);
				preList.clear();
			} else {
				advance();
			}
			switch (current.kind) {
			case TOKEN_ASSIGN:
				advance();
				parseExp();
				eatToken(Kind.TOKEN_SEMI);
				break;
			case TOKEN_LBRACK:
				advance();
				parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				eatToken(Kind.TOKEN_ASSIGN);
				parseExp();
				eatToken(Kind.TOKEN_SEMI);
				break;
			default:
				error(lexer.getFileName() + ":" + current.lineNum
						+ ":Expected assign or left-brack!");
				break;
			}
			break;
		default:
			error(lexer.getFileName() + ":" + current.lineNum
					+ ":Expected left-brace, if, while, System or identifier!");
			break;
		}
		production.pop();
		return;
	}

	// Statements -> Statement Statements
	// ->
	private void parseStatements() {
		production.push(Nonterminal.STATEMENT);
		if (!preList.isEmpty()) {
			current = preList.get(0);
		}
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {
			parseStatement();
		}
		production.pop();
		return;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private void parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		production.push(Nonterminal.TYPE);
		switch (current.kind) {
		case TOKEN_INT:
			advance();
			if (current.kind == Kind.TOKEN_LBRACK) {
				advance();
				eatToken(Kind.TOKEN_RBRACK);
			}
			break;
		case TOKEN_BOOLEAN:
			advance();
			break;
		case TOKEN_ID:
			preList.add(current);
			advance();
			break;
		default:
			error(lexer.getFileName() + ":" + current.lineNum
					+ ":Expected int, boolean or identifier!");
			break;
		}
		production.pop();
		return;
	}

	// VarDecl -> Type id ;
	private void parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		production.push(Nonterminal.VARDECL);
		parseType();
		if (current.kind == Kind.TOKEN_ID) {
			preList.clear();
			eatToken(Kind.TOKEN_ID);
			eatToken(Kind.TOKEN_SEMI);
		} else {
			preList.add(current);
		}
		// eatToken(Kind.TOKEN_ID);
		// eatToken(Kind.TOKEN_SEMI);
		production.pop();
		return;
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private void parseVarDecls() {
		production.push(Nonterminal.VARDECL);
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			parseVarDecl();
		}
		production.pop();
		return;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private void parseFormalList() {
		production.push(Nonterminal.FORMALLIST);
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			parseType();
			preList.clear();
			eatToken(Kind.TOKEN_ID);
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				parseType();
				preList.clear();
				eatToken(Kind.TOKEN_ID);
			}
		}
		production.pop();
		return;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private void parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		production.push(Nonterminal.METHODDECL);
		eatToken(Kind.TOKEN_PUBLIC);
		parseType();
		preList.clear();
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		parseVarDecls();
		parseStatements();
		eatToken(Kind.TOKEN_RETURN);
		parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return;
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private void parseMethodDecls() {
		production.push(Nonterminal.METHODDECL);
		while (current.kind == Kind.TOKEN_PUBLIC) {
			parseMethod();
		}
		production.pop();
		return;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private void parseClassDecl() {
		production.push(Nonterminal.CLASSDECL);
		eatToken(Kind.TOKEN_CLASS);
		eatToken(Kind.TOKEN_ID);
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		parseVarDecls();
		parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return;
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private void parseClassDecls() {
		production.push(Nonterminal.CLASSDECL);
		while (current.kind == Kind.TOKEN_CLASS) {
			parseClassDecl();
		}
		production.pop();
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
		production.push(Nonterminal.MAINCLASS);
		eatToken(Kind.TOKEN_CLASS);
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		parseStatements();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return;
	}

	// Program -> MainClass ClassDecl*
	private void parseProgram() {
		production.push(Nonterminal.PROGRAM);
		parseMainClass();
		parseClassDecls();
		eatToken(Kind.TOKEN_EOF);
		production.pop();
		return;
	}

  public ast.program.T parse()
  {
    parseProgram();
    System.out.println("Compilation completed!");
	if (errors.isEmpty()) {
		System.out.println("There is no syntax error.");
	} else {
		System.out.println("There is " + errors.size() + " syntax errors.");
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
		}
	}
    return null;
  }

}
