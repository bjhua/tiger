package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser
{
  Lexer lexer;
  Token current;
  Token backup;
  Token backup2;
  int line;
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

  private void eatToken(Kind kind)
  {
	while(current.kind == Kind.TOKEN_ANNO)
	{
		advance();
	}
    if (kind == current.kind)				
      advance();
    else {
      error();
      System.out.println("Expects: " + kind.toString());
      System.out.println("But got: " + current.kind.toString());
      System.exit(1);
    }
  }
  private void error()
  {
    System.out.println("Syntax error: compilation aborting...");
    System.out.println("error: At line "+(current.lineNum-line + 1));
   // System.exit(1);
    return;
  }

  // ////////////////////////////////////////////////////////////
  // below are method for parsing.

  // A bunch of parsing methods to parse expressions. The messy
  // parts are to deal with precedence and associativity.

  // ExpList -> Exp ExpRest*
  // ->
  // ExpRest -> , Exp
  private void parseExpList()
  {
    if (current.kind == Kind.TOKEN_RPAREN)            //碰到右括号退出
      return;
    parseExp();
    anno();
    while (current.kind == Kind.TOKEN_COMMER) {
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
  private void parseAtomExp()
  {
    switch (current.kind) {
    case TOKEN_LPAREN:
      advance();
      parseExp();
      eatToken(Kind.TOKEN_RPAREN);
      return;
    case TOKEN_NUM:
      advance();
      return;
    case TOKEN_TRUE:
      advance();
      return;
    case TOKEN_FALSE:
        advance();
        return;  
    case TOKEN_THIS:
      advance();
      return;
    case TOKEN_ID:
      advance();
      return;
    case TOKEN_NEW: {
      advance();
      switch (current.kind) {
      case TOKEN_INT:
        advance();
        eatToken(Kind.TOKEN_LBRACK);
        parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return;
      case TOKEN_ID:
        advance();
        eatToken(Kind.TOKEN_LPAREN);
        eatToken(Kind.TOKEN_RPAREN);
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
  private void parseNotExp()
  {
    parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
      if (current.kind == Kind.TOKEN_DOT) {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) {
          advance();
          return;
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
    return;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp
  private void parseTimesExp()
  {
    while (current.kind == Kind.TOKEN_NOT) {
      advance();
    }
    parseNotExp();
    return;
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExp
  private void parseAddSubExp()
  {
    parseTimesExp();
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      parseTimesExp();
    }
    return;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private void parseLtExp()
  {
    parseAddSubExp();
    while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
      advance();
      parseAddSubExp();
    }
    return;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private void parseAndExp()
  {
    parseLtExp();
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      parseLtExp();
    }
    return;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private void parseExp()
  {
    parseAndExp();
    while (current.kind == Kind.TOKEN_AND) {
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
  private void parseStatement()
  {
	   switch(current.kind)
		{
		   case TOKEN_LBRACE:
			   eatToken(Kind.TOKEN_LBRACE);
			   while(current.kind!=Kind.TOKEN_LBRACE)
			    parseStatement();
			   eatToken(Kind.TOKEN_RBRACE);
			   return ;
		   case TOKEN_IF:
		   {
			   eatToken(Kind.TOKEN_IF);
			   eatToken(Kind.TOKEN_LPAREN);
			   parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   parseStatement();
			   eatToken(Kind.TOKEN_ELSE);
			   parseStatement();
			   return ;
		   }
		   case TOKEN_WHILE:
		   {
			   eatToken(Kind.TOKEN_WHILE);
			   eatToken(Kind.TOKEN_LPAREN);
			   parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   parseStatement();
			   return ;
		   }   
		   case TOKEN_SYSTEM:
		   {
			   eatToken(Kind.TOKEN_SYSTEM);
			   eatToken(Kind.TOKEN_DOT);
			   eatToken(Kind.TOKEN_OUT);
			   eatToken(Kind.TOKEN_DOT);
			   eatToken(Kind.TOKEN_PRINTLN);
			   eatToken(Kind.TOKEN_LPAREN);
			   parseExp();
			   eatToken(Kind.TOKEN_RPAREN);
			   eatToken(Kind.TOKEN_SEMI);
			   return ;
		   }
		   case TOKEN_ID:
		   {
			   eatToken(Kind.TOKEN_ID);
			   switch(current.kind)
			   {
			     case TOKEN_ASSIGN:
			     {
			    	 advance();
					 parseExp();
					 eatToken(Kind.TOKEN_SEMI);
					 return ;
			     }
			     case TOKEN_LBRACK:
			     {
			    	 advance();
					 parseExp();
					 eatToken(Kind.TOKEN_RBRACK);
					 eatToken(Kind.TOKEN_ASSIGN);
					 parseExp();
					 eatToken(Kind.TOKEN_SEMI);
					 System.out.println("I win !");
					 System.out.println("");
					 return ;
			     }
			   }
		    }
		   case TOKEN_ASSIGN:
		    {
		    	 advance();
				 parseExp();
				 eatToken(Kind.TOKEN_SEMI);
				 return ;
		    }
		 }
	  return ;
  }

  // Statements -> Statement Statements
  // ->
  private void parseStatements()
  {
	anno();  
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID 
        || current.kind == Kind.TOKEN_ASSIGN) 
    	{
    	   parseStatement();
    	}
    return;
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  // write by zmk
  private void parseType()
  {
//	  if ((current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
//		        || current.kind == Kind.TOKEN_ID) && flag ==0)
//	  {   
		  advance();				
		//  anno();
		  if(current.kind == Kind.TOKEN_LBRACK)
		  {
			  eatToken(Kind.TOKEN_LBRACK);
			  eatToken(Kind.TOKEN_RBRACK);
		  }
//	  }
	  return ;
  }
  // VarDecl -> Type id ;
  // 一条语句定义一个变量
  private void parseVarDecl()
  {
    // to parse the "Type" nonterminal in this method, instead of writing
    // a fresh one.
    parseType();
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_SEMI);
    return;
  }

  // VarDecls -> VarDecl VarDecls
  // ->  多条语句分别连续定义多个变量
  private void parseVarDecls()
  {
	  anno();
	//  System.out.println(current.kind);
	  switch(current.kind)
	  {
	    case TOKEN_ID:
	    {
	    	advance();
	    	if(current.kind == Kind.TOKEN_ID)
	    	{
	    		eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    		break;
	    	}
	    	else{
	    		return ;
	    	}
	    }
	    case TOKEN_INT:
	    {
	    	advance();
	    	if(current.kind == Kind.TOKEN_LBRACK)
	    	{
	    		advance();
	    		eatToken(Kind.TOKEN_RBRACK);
	    		eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    	}
	    	else
	    	{
	    		eatToken(Kind.TOKEN_ID);
	    		eatToken(Kind.TOKEN_SEMI);
	    	}
	    	break;
	    }
	    case TOKEN_BOOLEAN:
	    {
	    	eatToken(Kind.TOKEN_BOOLEAN);
	    	eatToken(Kind.TOKEN_ID);
    		eatToken(Kind.TOKEN_SEMI);
    		break;
	    }	
	  }
	  if(current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN ||
	     current.kind == Kind.TOKEN_ID)
	  {
		  parseVarDecls();
	  }
	  return ;
  }

  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id     一条语句中定义一个或多个变量
  private void parseFormalList()
  {
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
      parseType();
      eatToken(Kind.TOKEN_ID);
      while (current.kind == Kind.TOKEN_COMMER) {
    	eatToken(Kind.TOKEN_COMMER);
        parseType();
        eatToken(Kind.TOKEN_ID);
      }
    }
    return;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  // Write by zmk
  private void parseMethod()
  {
    	eatToken(Kind.TOKEN_PUBLIC);
    	parseType();
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
    	anno();
    	return;
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private void parseMethodDecls()
  {
	  anno();
	  while (current.kind == Kind.TOKEN_PUBLIC) {
        parseMethod();
    }
    return;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private void parseClassDecl()
  {
	anno();
    eatToken(Kind.TOKEN_CLASS);
    eatToken(Kind.TOKEN_ID);
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      eatToken(Kind.TOKEN_ID);
    }
    eatToken(Kind.TOKEN_LBRACE);
    anno();
    parseVarDecls();
    parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    anno();
    return;
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private void parseClassDecls()
  {
	  anno();
	  while (current.kind == Kind.TOKEN_CLASS) {
         parseClassDecl();
    }
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private void parseMainClass()
  {
    line = current.lineNum;	  
	anno();
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
  }

  // Program -> MainClass ClassDecl*
  private void parseProgram()
  {
    parseMainClass();
    parseClassDecls();
    eatToken(Kind.TOKEN_EOF);
    return;
  }

  public ast.Ast.Program.T parse()
  {
    parseProgram();
    return null;
  }
}
