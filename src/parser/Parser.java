package parser;

import java.util.LinkedList;

import ast.Ast.Type.*;
import ast.Ast;
import ast.Ast.Class;
import ast.Ast.Dec;
import ast.Ast.Exp;
import ast.Ast.MainClass;
import ast.Ast.Method;
import ast.Ast.Program;
import ast.Ast.Stm;
import ast.Ast.Program.T;
import ast.Ast.Type;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import ast.Ast.Exp.*;
import ast.Ast.Stm.*;
public class Parser
{
  Lexer lexer;
  Token current;
  Token backup;
  Token backup2;
  int line;
  String strl = null;
  public Parser(String fname, java.io.InputStream fstream)
  {
    lexer = new Lexer(fname, fstream);
    current = lexer.nextToken();
  }
  //搞定annotation
  private void anno()
  {
	  while(current.kind==Kind.TOKEN_ANNO)
	  {
		  advance();
	  }
  } 
  
  // /////////////////////////////////////////////
  // utility methods to connect the lexer
  // and the parser.
  //read the next Token
  private void advance()
  {
    current = lexer.nextToken();
    anno();
  }

  private String eatToken(Kind kind)
  {
	String str = null;
	while(current.kind == Kind.TOKEN_ANNO)
	{
		advance();
	}
    if (kind == current.kind)
    {
      if(current.lexeme!=null)
         str = new String(current.lexeme);
      advance();
      return str;
    }
    else {
      error();
      System.out.println("Expects: " + kind.toString());
      System.out.println("But got: " + current.kind.toString());
      System.exit(1);
      return null;
    }
  }
  private void error()
  {
    System.out.println("Syntax error: compilation aborting...");
    System.out.println("error: At line "+(current.lineNum-line + 1));
    return;
  }

  // ////////////////////////////////////////////////////////////
  // below are method for parsing.

  // A bunch of parsing methods to parse expressions. The messy
  // parts are to deal with precedence and associativity.

  // ExpList -> Exp ExpRest*
  // ->
  // ExpRest -> , Exp
  private LinkedList<ast.Ast.Exp.T>  parseExpList()
  {
	LinkedList<ast.Ast.Exp.T> Explist = new LinkedList<ast.Ast.Exp.T>();
    if (current.kind == Kind.TOKEN_RPAREN)            //碰到右括号退出
       return Explist;
    Exp.T b = parseExp();
    Explist.add(b);
    anno();
    while (current.kind == Kind.TOKEN_COMMER) 
    {
      advance();
      b = parseExp();
      Explist.add(b);
    }
    return Explist;
  }

  // AtomExp -> (exp)
  // -> INTEGER_LITERAL	 
  // -> true
  // -> false
  // -> this
  // -> id
  // -> new int [exp]
  // -> new id ()
  private Exp.T parseAtomExp()
  {
    switch (current.kind) {
    case TOKEN_LPAREN:
      advance();
      Exp.T a1 = parseExp();
      eatToken(Kind.TOKEN_RPAREN);
      return a1;
    case TOKEN_NUM:
      int num1 = Integer.valueOf(current.lexeme);
      advance();
      return new Num(num1);
    case TOKEN_TRUE:
      advance();
      return new Exp.True();
    case TOKEN_FALSE:
        advance();
      return new Exp.False();  
    case TOKEN_THIS:
      advance();
      return new Exp.This();
    case TOKEN_ID:
      String str1 = current.lexeme;
      advance();
      return new Exp.Id(str1,current.lineNum-line + 1);
    case TOKEN_NEW: {
      advance();
      switch (current.kind) {
      case TOKEN_INT:
        advance();
        eatToken(Kind.TOKEN_LBRACK);
        Exp.T a2 = parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return new NewIntArray(a2,current.lineNum-line + 1);
      case TOKEN_ID:
    	String str2 = eatToken(Kind.TOKEN_ID);
        eatToken(Kind.TOKEN_LPAREN);
        eatToken(Kind.TOKEN_RPAREN);
        return new NewObject(str2,current.lineNum-line + 1);
      default:
        error();
        return null;
      }
    }
    default:
      error();
      return null;
    }
  }

  // NotExp -> AtomExp
  // -> AtomExp .id (expList)
  // -> AtomExp [exp]
  // -> AtomExp .length
  private Exp.T parseNotExp()
  {
    Exp.T b1 = parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
      if (current.kind == Kind.TOKEN_DOT)
      {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) 
        {
          advance();
          return new Exp.Length(b1,current.lineNum-line + 1);
        }
        String b3 = eatToken(Kind.TOKEN_ID);
        eatToken(Kind.TOKEN_LPAREN);
        LinkedList<ast.Ast.Exp.T> b4 = parseExpList();
        eatToken(Kind.TOKEN_RPAREN);
        return new Exp.Call(b1, b3, b4,current.lineNum-line + 1);
      } 
      else 
      {
        advance();
        Exp.T b5 = parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return new ArraySelect(b1,b5,current.lineNum-line + 1);
      }
    }
    return b1;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp 
  private Exp.T parseTimesExp()
  {
	int i = 0;
    while (current.kind == Kind.TOKEN_NOT) {
      advance();
      i++;
    }
    Exp.T b6 = parseNotExp();
    while(i--!=0)
    {
      b6 = new Not(b6,current.lineNum-line + 1);
    }
    return b6;
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExpdd
  private Exp.T parseAddSubExp()
  {
    Exp.T b7 = parseTimesExp();
    Exp.T b8 = null;
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      b8 = parseTimesExp(); 
      b7 =new Times(b7,b8,current.lineNum-line + 1);
    }
    return b7;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private Exp.T parseLtExp()
  {
    Exp.T b9 = parseAddSubExp();
    Exp.T b10 = null;
    while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB)
    {
      Kind q = current.kind;
      advance();
      b10 = parseAddSubExp();
      if(q == Kind.TOKEN_ADD)
         b9 = new Exp.Add(b9,b10,current.lineNum-line + 1);
      else
    	 b9 = new Exp.Sub(b9,b10,current.lineNum-line + 1);
    }
    return b9;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private Exp.T parseAndExp()
  {
    Exp.T b11 = parseLtExp();
    Exp.T b12 = null;
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      b12 = parseLtExp();
      b11 = new Exp.Lt(b11, b12,current.lineNum-line + 1);
    }
    return b11;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private Exp.T parseExp()
  {
    Exp.T b13 = parseAndExp();
    Exp.T b14 = null;
    while (current.kind == Kind.TOKEN_AND) {
      advance();
      b14 = parseAndExp();
      b13 = new Exp.And(b13, b14,current.lineNum-line + 1);
    }
    return b13;
  }

  // Statement -> { Statement* }
  // -> if ( Exp ) Statement else Statement
  // -> while ( Exp ) Statement
  // -> System.out.println ( Exp ) ;
  // -> id = Exp ;
  // -> id [ Exp ]= Exp ;    //ast.Ast.Stm.T
  private ast.Ast.Stm.T parseStatement()
  {
	   ast.Ast.Stm.T c1;
	   java.util.LinkedList<Stm.T> stmss = new java.util.LinkedList<Stm.T> ();
	   switch(current.kind)
		{
		   case TOKEN_LBRACE:
		   {
			   eatToken(Kind.TOKEN_LBRACE);
			   while(current.kind!=Kind.TOKEN_RBRACE)
			   {
			       c1 = parseStatement();
			       stmss.add(c1);
			   }
			   eatToken(Kind.TOKEN_RBRACE);
			   return new Stm.Block(stmss);
		   }	
		   case TOKEN_IF:
		   {
			   eatToken(Kind.TOKEN_IF);
			   int lineNumm = current.lineNum-line+1;
			   eatToken(Kind.TOKEN_LPAREN);
			   Exp.T c2 = parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   Stm.T c3 = parseStatement();
			   eatToken(Kind.TOKEN_ELSE);
			   Stm.T c4 = parseStatement();
			   return new Stm.If(c2, c3, c4,lineNumm);
		   }
		   case TOKEN_WHILE:
		   {
			   eatToken(Kind.TOKEN_WHILE);
			   int lineNumm = current.lineNum-line+1;
			   eatToken(Kind.TOKEN_LPAREN);
			   Exp.T c5 = parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   Stm.T c6 = parseStatement();
			   return new Stm.While(c5,c6,lineNumm);
		   }   
		   case TOKEN_SYSTEM:
		   {
			   eatToken(Kind.TOKEN_SYSTEM);
			   eatToken(Kind.TOKEN_DOT);
			   eatToken(Kind.TOKEN_OUT);
			   eatToken(Kind.TOKEN_DOT);
			   eatToken(Kind.TOKEN_PRINTLN);
			   eatToken(Kind.TOKEN_LPAREN);
			   Exp.T c7 = parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   eatToken(Kind.TOKEN_SEMI);
			   return new Stm.Print(c7,current.lineNum-line);
		   }
		   case TOKEN_ID:
		   {
			   String c8 = eatToken(Kind.TOKEN_ID);
			   switch(current.kind)
			   {
			     case TOKEN_ASSIGN:
			     {
			    	 advance();
					 Exp.T c9 = parseExp();              
					 eatToken(Kind.TOKEN_SEMI);
					 return new Ast.Stm.Assign(c8,c9,current.lineNum-line );
			     }
			     case TOKEN_LBRACK:
			     {
			    	 advance();
					 Exp.T c10 = parseExp();
					 eatToken(Kind.TOKEN_RBRACK);
					 eatToken(Kind.TOKEN_ASSIGN);
					 Exp.T c11 = parseExp();
					 eatToken(Kind.TOKEN_SEMI);
					 return new Stm.AssignArray(c8, c10, c11,current.lineNum-line + 1);
			     }
			   }
		    }
		   case TOKEN_ASSIGN:
		    {
		    	 advance();
				 Exp.T c11 = parseExp();
				 eatToken(Kind.TOKEN_SEMI);
				 return new Stm.Assign(strl, c11,current.lineNum-line + 1);
		    }
		 }
	  return null;
  }

  // Statements -> Statement Statements
  // ->
  private LinkedList<Stm.T> parseStatements()
  {
	anno(); 
	java.util.LinkedList<Stm.T> stms = new java.util.LinkedList<Stm.T>();
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID 
        || current.kind == Kind.TOKEN_ASSIGN) 
    	{		
    	   Stm.T stm = parseStatement();
    	   stms.add(stm);
    	}
    return stms;
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  // write by zmk
  private Type.T parseType()
  {
	      if(current.kind == Kind.TOKEN_BOOLEAN)
	      {
	    	  advance();
	    	  return new Type.Boolean();
	      }
	      else if(current.kind == Kind.TOKEN_ID)
	      {
	    	  String g2 = eatToken(Kind.TOKEN_ID);
	    	  return new Type.ClassType(g2);
	      }
	      else if(current.kind == Kind.TOKEN_INT)
	      {
		      advance();				
		      if(current.kind == Kind.TOKEN_LBRACK)
		      {
		    	  eatToken(Kind.TOKEN_LBRACK);
		    	  eatToken(Kind.TOKEN_RBRACK);
		    	  return new Type.IntArray();
		      }
		      else
		      {
		    	  return new Type.Int();
		      }
	      }
	  return null;
  }
  // VarDecl -> Type id ;
  // 一条语句定义一个变量


  // VarDecls -> VarDecl VarDecls
  // ->  多条语句分别连续定义多个变量
  private java.util.LinkedList<Dec.T> parseVarDecls()
  {
	  anno();
	  LinkedList<Ast.Dec.T> stms = new LinkedList<Ast.Dec.T>();
	  switch(current.kind)
	  {
	    case TOKEN_ID:
	    {
	    	String d1 = eatToken(Kind.TOKEN_ID);
	    	if(current.kind == Kind.TOKEN_ID)
	    	{
	    		String d2 = eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    		stms.add(new Dec.DecSingle(new ClassType(d1), d2,current.lineNum-line));
	    		break;
	    	}
	    	else{
	    		strl = d1;
	    	    return stms;
	    	}
	    }
	    case TOKEN_INT:
	    {
	    	advance();
	    	if(current.kind == Kind.TOKEN_LBRACK)
	    	{
	    		advance();
	    		eatToken(Kind.TOKEN_RBRACK);
	    		String d3 = eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    		stms.add(new Dec.DecSingle(new IntArray(), d3,current.lineNum-line));
	    	}
	    	else
	    	{
	    		String d4 = eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    		stms.add(new Dec.DecSingle(new Int(), d4,current.lineNum-line));
	    	}
	    	break;
	    }
	    case TOKEN_BOOLEAN:
	    {
	    	eatToken(Kind.TOKEN_BOOLEAN);
	    	String d5 = eatToken(Kind.TOKEN_ID);
    		eatToken(Kind.TOKEN_SEMI);
    		stms.add(new Dec.DecSingle(new Ast.Type.Boolean(),d5,current.lineNum-line));
    		break;
	    }	
	  }
	  if(current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN ||
	     current.kind == Kind.TOKEN_ID)
	  {
		  LinkedList<Ast.Dec.T> stmsss  = parseVarDecls();
		  if(stmsss!=null)
	       stms.addAll(stmsss);		  
	  }
	  return stms;
  }
  
  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id     一条语句中定义一个或多个变量
  private LinkedList<Dec.T> parseFormalList()
  {
	LinkedList<Dec.T>  h = new LinkedList<Dec.T>();
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) 
    {
      Type.T formtype = parseType();
      String formalid = eatToken(Kind.TOKEN_ID);
      h.add(new Dec.DecSingle(formtype, formalid, current.lineNum-line + 1));
      while (current.kind == Kind.TOKEN_COMMER) 
      {
    	eatToken(Kind.TOKEN_COMMER);
        formtype = parseType();
        formalid = eatToken(Kind.TOKEN_ID);
        h.add(new Dec.DecSingle(formtype, formalid, current.lineNum-line + 1));
      }
    }
    return h;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  // Write by zmk
  private Method.T parseMethod()
  {
    	eatToken(Kind.TOKEN_PUBLIC);
    	Type.T f1 = parseType();
    	String methid = eatToken(Kind.TOKEN_ID);
    	eatToken(Kind.TOKEN_LPAREN);
    	LinkedList<Dec.T> formals = parseFormalList();
    	eatToken(Kind.TOKEN_RPAREN);
    	eatToken(Kind.TOKEN_LBRACE);
    	java.util.LinkedList<Dec.T> methodDecls = parseVarDecls();
    	LinkedList<Stm.T> methodStm = parseStatements();
    	eatToken(Kind.TOKEN_RETURN);
    	Exp.T methodExp = parseExp();
    	eatToken(Kind.TOKEN_SEMI);
    	eatToken(Kind.TOKEN_RBRACE);
    	anno();
    	return new Method.MethodSingle(f1, methid, formals, methodDecls, methodStm, methodExp);
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private java.util.LinkedList<ast.Ast.Method.T> parseMethodDecls()
  {
	  anno();
	  LinkedList<ast.Ast.Method.T> e1 = new LinkedList<ast.Ast.Method.T>();
	  while (current.kind == Kind.TOKEN_PUBLIC) {
        Method.T e2 = parseMethod();
        e1.add(e2);
    }
    return e1;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private Class.T parseClassDecl()
  {
	anno();
	String extend;
    eatToken(Kind.TOKEN_CLASS);
    String classid2 = eatToken(Kind.TOKEN_ID);
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      extend = eatToken(Kind.TOKEN_ID);
    }
    else
    {
      extend = null;
    }
    eatToken(Kind.TOKEN_LBRACE);
    anno();
    java.util.LinkedList<Dec.T> Vardecls = parseVarDecls();
    java.util.LinkedList<ast.Ast.Method.T> MethodDecls = parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    anno();
    return new Class.ClassSingle(classid2,extend, Vardecls, MethodDecls);
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private LinkedList<Class.T> parseClassDecls()
  {
	  anno();
	  LinkedList<Class.T> Listclass = new LinkedList<Class.T>();
	  while (current.kind == Kind.TOKEN_CLASS) {
         Class.T clas = parseClassDecl();
         Listclass.add(clas);
    }
	  return Listclass;
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private MainClass.T parseMainClass()
  {
    line = current.lineNum;	  
	anno();
	eatToken(Kind.TOKEN_CLASS);
	String classid = eatToken(Kind.TOKEN_ID);
	eatToken(Kind.TOKEN_LBRACE);
	eatToken(Kind.TOKEN_PUBLIC);
	eatToken(Kind.TOKEN_STATIC);
	eatToken(Kind.TOKEN_VOID);
	eatToken(Kind.TOKEN_MAIN);
	eatToken(Kind.TOKEN_LPAREN);
	eatToken(Kind.TOKEN_STRING);
	eatToken(Kind.TOKEN_LBRACK);
	eatToken(Kind.TOKEN_RBRACK);
	String args = eatToken(Kind.TOKEN_ID);
	eatToken(Kind.TOKEN_RPAREN);
	eatToken(Kind.TOKEN_LBRACE);
	LinkedList<Stm.T> mainstm = parseStatements();
	eatToken(Kind.TOKEN_RBRACE);
	eatToken(Kind.TOKEN_RBRACE);
	return new MainClass.MainClassSingle(classid,args,new Block(mainstm));
  }

  // Program -> MainClass ClassDecl*
  private Program.T parseProgram()
  {
	MainClass.T class1 = parseMainClass();
	LinkedList<Class.T> list1 = parseClassDecls();
    eatToken(Kind.TOKEN_EOF);
    return new Program.ProgramSingle(class1,list1);
  }

  public ast.Ast.Program.T parse()
  {
    
    return parseProgram();
  }
}
