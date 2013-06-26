package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import ast.stm.If;
import ast.stm.While;

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
	private java.util.LinkedList<ast.exp.T> parseExpList() {
		production.push(Nonterminal.EXPLIST);
		java.util.LinkedList<ast.exp.T> expList = 
				new java.util.LinkedList<ast.exp.T>();
		if (current.kind == Kind.TOKEN_RPAREN) {
			production.pop();
			return expList;
		}
		expList.addLast(parseExp());
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			expList.add(parseExp());
		}
		production.pop();
		return expList;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.exp.T parseAtomExp() {
		production.push(Nonterminal.ATOMEXP);
		ast.exp.T exp = null;
		String id = null;
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			return exp;
		case TOKEN_NUM:
			Integer num = Integer.parseInt(current.lexeme);
			advance();
			return new ast.exp.Num(num.intValue());
		case TOKEN_TRUE:
			advance();
			return new ast.exp.True();
		case TOKEN_FALSE:
			advance();
			return new ast.exp.False();
		case TOKEN_THIS:
			advance();
			return new ast.exp.This();
		case TOKEN_ID:
			id = current.lexeme;
			advance();
			return new ast.exp.Id(id);
		case TOKEN_NEW:
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				exp = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.NewIntArray(exp);
			case TOKEN_ID:
				id = current.lexeme;
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.NewObject(id);
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
		return null;
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private ast.exp.T parseNotExp() {
		production.push(Nonterminal.NOTEXP);
		ast.exp.T exp = parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT
				|| current.kind == Kind.TOKEN_LBRACK) {
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					advance();
					return new ast.exp.Length(exp);
				}
				String id = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				java.util.LinkedList<ast.exp.T> args = parseExpList();
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.Call(exp, id, args);
			} else {
				advance();
				ast.exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.ArraySelect(exp, index);
			}
		}
		production.pop();
		return exp;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.exp.T parseTimesExp() {
		production.push(Nonterminal.TIMESEXP);
		ast.exp.T exp = null;
		if (current.kind == Kind.TOKEN_NOT) {
			while (current.kind == Kind.TOKEN_NOT) {
				advance();
				exp = new ast.exp.Not(parseTimesExp());
			}
		}else {
			exp = parseNotExp();
		}
		production.pop();
		return exp;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.exp.T parseAddSubExp() {
		production.push(Nonterminal.ADDSUBEXP);
		ast.exp.T left = parseTimesExp();
		ast.exp.T right = null;
		while (current.kind == Kind.TOKEN_TIMES) {
			advance();
			right = parseTimesExp();
		}
		production.pop();
		return new ast.exp.Times(left, right);
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.exp.T parseLtExp() {
		production.push(Nonterminal.LTEXP);
		ast.exp.T addSubExp = null;
		ast.exp.T left = parseAddSubExp();
		ast.exp.T right = null;
		/*while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
			advance();
			parseAddSubExp();
		}*/
		if (current.kind == Kind.TOKEN_ADD) {
			advance();
			right = parseAddSubExp();
			addSubExp = new ast.exp.Add(left, right);
		}else if (current.kind == Kind.TOKEN_SUB) {
			advance();
			right = parseAddSubExp();
			addSubExp = new ast.exp.Sub(left, right);
		}else {
			addSubExp = left;
		}
		production.pop();
		return addSubExp;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.exp.T parseAndExp() {
		production.push(Nonterminal.ANDEXP);
		ast.exp.T left = parseLtExp();
		ast.exp.T right = null;
		while (current.kind == Kind.TOKEN_LT) {
			advance();
			right = parseLtExp();
		}
		production.pop();
		return new ast.exp.Lt(left, right);
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.exp.T parseExp() {
		production.push(Nonterminal.EXP);
		ast.exp.T left = parseAndExp();
		ast.exp.T right = null;
		while (current.kind == Kind.TOKEN_AND) {
			advance();
			right = parseAndExp();
		}
		production.pop();
		return new ast.exp.And(left, right);
	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.stm.T parseStatement() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.
		production.push(Nonterminal.STATEMENT);
		switch (current.kind) {
		case TOKEN_LBRACE:
			advance();
			java.util.LinkedList<ast.stm.T> stms = 
					new java.util.LinkedList<ast.stm.T>();
			while (current.kind != Kind.TOKEN_RBRACE) {
				stms = parseStatements();
			}
			advance();
			return new ast.stm.Block(stms);
		case TOKEN_IF:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T ifCondition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T thenn = parseStatement();
			eatToken(Kind.TOKEN_ELSE);
			ast.stm.T elsee = parseStatement();
			return new If(ifCondition, thenn, elsee);
		case TOKEN_WHILE:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T whileCondition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T body = parseStatement();
			return new While(whileCondition, body);
		case TOKEN_SYSTEM:
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T printExp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			eatToken(Kind.TOKEN_SEMI);
			return new ast.stm.Print(printExp);
		case TOKEN_ID:
			String id = current.lexeme;
			if (!preList.isEmpty()) {
				current = preList.get(1);
				preList.clear();
			} else {
				advance();
			}
			switch (current.kind) {
			case TOKEN_ASSIGN:
				advance();
				ast.exp.T assignExp = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.Assign(id, assignExp);
			case TOKEN_LBRACK:
				advance();
				ast.exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				eatToken(Kind.TOKEN_ASSIGN);
				ast.exp.T arrayExp = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.AssignArray(id, index, arrayExp);
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
		return null;
	}

	// Statements -> Statement Statements
	// ->
	private java.util.LinkedList<ast.stm.T> parseStatements() {
		production.push(Nonterminal.STATEMENT);
		java.util.LinkedList<ast.stm.T> stms = 
				new java.util.LinkedList<ast.stm.T>();
		if (!preList.isEmpty()) {
			current = preList.get(0);
		}
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {
			stms.add(parseStatement());
		}
		production.pop();
		return stms;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.type.T parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		production.push(Nonterminal.TYPE);
		switch (current.kind) {
		case TOKEN_INT:
			advance();
			if (current.kind == Kind.TOKEN_LBRACK) {
				advance();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.type.IntArray();
			}
			return new ast.type.Int();
		case TOKEN_BOOLEAN:
			advance();
			return new ast.type.Boolean();
		case TOKEN_ID:
			preList.add(current);
			String id = current.lexeme;
			advance();
			return new ast.type.Class(id);
		default:
			error(lexer.getFileName() + ":" + current.lineNum
					+ ":Expected int, boolean or identifier!");
			break;
		}
		production.pop();
		return null;
	}

	// VarDecl -> Type id ;
	private ast.dec.T parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		production.push(Nonterminal.VARDECL);
		ast.type.T type = parseType();
		String id = new String();
		if (type != null && current.kind == Kind.TOKEN_ID) {
			preList.clear();
			id = current.lexeme;
			int lineNum = current.lineNum;
			eatToken(Kind.TOKEN_ID);
			eatToken(Kind.TOKEN_SEMI);
			production.pop();
			return new ast.dec.Dec(type, id, lineNum);
		} else {
			preList.add(current);
			return null;
		}
		// eatToken(Kind.TOKEN_ID);
		// eatToken(Kind.TOKEN_SEMI);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private java.util.LinkedList<ast.dec.T> parseVarDecls() {
		production.push(Nonterminal.VARDECL);
		java.util.LinkedList<ast.dec.T> varDecls = 
				new java.util.LinkedList<ast.dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.dec.T varDecl = parseVarDecl();
			if (varDecl != null) {
				varDecls.add(varDecl);
			}
		}
		production.pop();
		return varDecls;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private java.util.LinkedList<ast.dec.T> parseFormalList() {
		production.push(Nonterminal.FORMALLIST);
		java.util.LinkedList<ast.dec.T> formals = 
				new java.util.LinkedList<ast.dec.T>();
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.type.T type = parseType();
			preList.clear();
			String id = current.lexeme;
			int lineNum = current.lineNum;
			eatToken(Kind.TOKEN_ID);
			formals.add(new ast.dec.Dec(type, id, lineNum));
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				type = parseType();
				preList.clear();
				id = current.lexeme;
				lineNum = current.lineNum;
				eatToken(Kind.TOKEN_ID);
				formals.add(new ast.dec.Dec(type, id, lineNum));
			}
		}
		production.pop();
		return formals;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.method.T parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		production.push(Nonterminal.METHODDECL);
		eatToken(Kind.TOKEN_PUBLIC);
		ast.type.T retType = parseType();
		preList.clear();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		java.util.LinkedList<ast.dec.T> formals = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		java.util.LinkedList<ast.dec.T> locals = parseVarDecls();
		java.util.LinkedList<ast.stm.T> stms = parseStatements();
		eatToken(Kind.TOKEN_RETURN);
		ast.exp.T retExp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return new ast.method.Method(retType, id, formals, locals, stms, retExp);
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private java.util.LinkedList<ast.method.T> parseMethodDecls() {
		production.push(Nonterminal.METHODDECL);
		java.util.LinkedList<ast.method.T> methods = 
				new java.util.LinkedList<ast.method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			methods.add(parseMethod());
		}
		production.pop();
		return methods;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.classs.Class parseClassDecl() {
		production.push(Nonterminal.CLASSDECL);
		eatToken(Kind.TOKEN_CLASS);
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		String extendss = null;
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			extendss = current.lexeme;
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		java.util.LinkedList<ast.dec.T> decs = parseVarDecls();
		java.util.LinkedList<ast.method.T> methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return new ast.classs.Class(id, extendss, decs, methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private java.util.LinkedList<ast.classs.T> parseClassDecls() {
		production.push(Nonterminal.CLASSDECL);
		java.util.LinkedList<ast.classs.T> classDecls = 
				new java.util.LinkedList<ast.classs.T>();
		while (current.kind == Kind.TOKEN_CLASS) {
			classDecls.add(parseClassDecl());
		}
		production.pop();
		return classDecls;
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.mainClass.MainClass parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
		production.push(Nonterminal.MAINCLASS);
		eatToken(Kind.TOKEN_CLASS);
		String id = current.lexeme;
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
		String arg = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		ast.stm.T stm = parseStatement();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		production.pop();
		return new ast.mainClass.MainClass(id, arg, stm);
	}

	// Program -> MainClass ClassDecl*
	private ast.program.T parseProgram() {
		production.push(Nonterminal.PROGRAM);
		ast.mainClass.MainClass mainClass = parseMainClass();
		java.util.LinkedList<ast.classs.T> classDecls =  parseClassDecls();
		eatToken(Kind.TOKEN_EOF);
		production.pop();
		return new ast.program.Program(mainClass, classDecls);
	}

  public ast.program.T parse()
  {
    ast.program.T ast = parseProgram();
    System.out.println("Compilation completed!");
	if (errors.isEmpty()) {
		System.out.println("There is no syntax error.");
	} else {
		System.out.println("There is " + errors.size() + " syntax errors.");
		for (int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i));
		}
	}
    return ast;
  }

}
