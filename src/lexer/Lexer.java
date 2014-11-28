package lexer;

import static control.Control.ConLexer.dump;

import java.time.format.ResolverStyle;
import java.util.*;
import java.io.InputStream;
import java.util.Hashtable;

import lexer.Token.Kind;
import util.Todo;

public class Lexer
{  
  String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file

  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
  }
  
  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  static int line = 1;
  Hashtable<String,String> res = new Hashtable<String,String>();
  Hashtable<String,Token.Kind> res2 = new Hashtable<String,Token.Kind>();
  Hashtable<String,String> res3 = new Hashtable<String,String>();
  Hashtable<String,Token.Kind> res4 = new Hashtable<String,Token.Kind>();
  public void initsmallTable()
  {
	  res3.put("+","+");
	  res3.put("=","=");
	  res3.put(",",",");
	  res3.put(".",".");
	  res3.put("{","{");
	  res3.put("[","[");
	  res3.put("(","(");
	  res3.put("<","<");
	  res3.put("!","!");
	  res3.put("}","}");
	  res3.put("]","]");
	  res3.put(")",")");
	  res3.put(">",">");
	  res3.put(";",";");
	  res3.put("-","-");
	  res3.put("*","*");
  }
  public void initsmallTable2()
  {
	  res4.put("+", Kind.TOKEN_ADD);
	  res4.put("=", Kind.TOKEN_ASSIGN);
	  res4.put(",", Kind.TOKEN_COMMER);
	  res4.put(".", Kind.TOKEN_DOT);
	  res4.put("{", Kind.TOKEN_LBRACE);
	  res4.put("[", Kind.TOKEN_LBRACK);
	  res4.put("(", Kind.TOKEN_LPAREN);
	  res4.put("<", Kind.TOKEN_LT);
	  res4.put("!", Kind.TOKEN_NOT);
	  res4.put("}", Kind.TOKEN_RBRACE);
	  res4.put("]", Kind.TOKEN_RBRACK);
	  res4.put(")", Kind.TOKEN_RPAREN);
	  res4.put(">",Kind.TOKEN_RT);
	  res4.put(";", Kind.TOKEN_SEMI);
	  res4.put("-", Kind.TOKEN_SUB);
	  res4.put("*", Kind.TOKEN_TIMES);  
  }
  public void initTable()
  {
	  res.put("+","+");
	  res.put("&&","and");
	  res.put("=","=");
	  res.put("boolean", "boolean");
	  res.put("class","class");
	  res.put(",",",");
	  res.put(".",".");
	  res.put("else","else");
	  res.put("extends","extends");
	  res.put("false","false");
//	  res.put("Identifier","ID");
	  res.put("if","if");
	  res.put("int","int");
	  res.put("{","{");
	  res.put("[","[");
	  res.put("length","length");
	  res.put("(","(");
	  res.put("<","<");
	  res.put("main","main");
	  res.put("new","new");
	  res.put("!","!");
//	  res.put("IntegerLiteral","NUM");
	  res.put("out","out");
	  res.put("println","println");
	  res.put("public","public");
	  res.put("}","}");
	  res.put("]","]");
	  res.put("return","return");
	  res.put(")",")");
	  res.put(">",">");
	  res.put(";",";");
	  res.put("static","static");
	  res.put("String","String");
	  res.put("-","-");
	  res.put("System", "System");
	  res.put("this", "this");
	  res.put("*","*");
	  res.put("true","true");
	  res.put("void","void");
	  res.put("while","while");
  }
  public void initTable2()
  {
	  res2.put("+", Kind.TOKEN_ADD);
	  res2.put("&&",Kind.TOKEN_AND);
	  res2.put("=", Kind.TOKEN_ASSIGN);
	  res2.put("boolean",Kind.TOKEN_BOOLEAN );
	  res2.put("class",Kind.TOKEN_CLASS);
	  res2.put(",", Kind.TOKEN_COMMER);
	  res2.put(".", Kind.TOKEN_DOT);
	  res2.put("else",Kind.TOKEN_ELSE);
	  res2.put("extends",Kind.TOKEN_EXTENDS);
	  res2.put("false",Kind.TOKEN_FALSE);
//	  res2.put("Identifier",Kind.TOKEN_ID);
	  res2.put("if",Kind.TOKEN_IF);
	  res2.put("int",Kind.TOKEN_INT);
	  res2.put("{", Kind.TOKEN_LBRACE);
	  res2.put("[", Kind.TOKEN_LBRACK);
	  res2.put("length",Kind.TOKEN_LENGTH);
	  res2.put("(", Kind.TOKEN_LPAREN);
	  res2.put("<", Kind.TOKEN_LT);
	  res2.put("main",Kind.TOKEN_MAIN);
	  res2.put("new",Kind.TOKEN_NEW);
	  res2.put("!", Kind.TOKEN_NOT);
//	  res2.put("IntegerLiteral",Kind.TOKEN_NUM);
	  res2.put("out",Kind.TOKEN_OUT);
	  res2.put("println",Kind.TOKEN_PRINTLN);
	  res2.put("public",Kind.TOKEN_PUBLIC);
	  res2.put("}", Kind.TOKEN_RBRACE);
	  res2.put("]", Kind.TOKEN_RBRACK);
	  res2.put("return",Kind.TOKEN_RETURN);
	  res2.put(")", Kind.TOKEN_RPAREN);
	  res2.put(">",Kind.TOKEN_RT);
	  res2.put(";", Kind.TOKEN_SEMI);
	  res2.put("static",Kind.TOKEN_STATIC);
	  res2.put("String",Kind.TOKEN_STRING);
	  res2.put("-", Kind.TOKEN_SUB);
	  res2.put("System", Kind.TOKEN_SYSTEM);
	  res2.put("this", Kind.TOKEN_THIS);
	  res2.put("*", Kind.TOKEN_TIMES);
	  res2.put("true",Kind.TOKEN_TRUE);
	  res2.put("void",Kind.TOKEN_VOID);
	  res2.put("while",Kind.TOKEN_WHILE);
  }
  private Token nextTokenInternal() throws Exception
  {
	initTable();
	initTable2();
	initsmallTable();
	initsmallTable2();
    int c = this.fstream.read();
    StringBuffer str = new StringBuffer();
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, line);    
    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || '\n' == c) 
    {
      if('\n' == c)
    		line++;
      c = this.fstream.read();
    }
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, line);
    
    switch (c) {
    case '+':
        return new Token(Kind.TOKEN_ADD, line,"+");
    case '=':
    	return new Token(Kind.TOKEN_ASSIGN,line,"=");
    case ',':
        return new Token(Kind.TOKEN_COMMER,line,",");
    case '.':
        return new Token(Kind.TOKEN_DOT,line,".");
    case '{':
        return new Token(Kind.TOKEN_LBRACE,line,"{");
    case '[':
        return new Token(Kind.TOKEN_LBRACK,line,"[");
    case '(':
        return new Token(Kind.TOKEN_LPAREN,line,"(");
    case '<':
        return new Token(Kind.TOKEN_LT,line,"<"); 
    case '!':
        return new Token(Kind.TOKEN_NOT,line,"!");
    case '}':
        return new Token(Kind.TOKEN_RBRACE,line,"}");
    case ']':
        return new Token(Kind.TOKEN_RBRACK,line,"]");
    case ')':
        return new Token(Kind.TOKEN_RPAREN,line,")"); 
    case ';':
        return new Token(Kind.TOKEN_SEMI,line,";");
    case '-':
        return new Token(Kind.TOKEN_SUB,line,"-");
    case '*':
        return new Token(Kind.TOKEN_TIMES,line,"*");
    default:
    	char r1 = (char)c;
        str.append(r1);
        
        //判断是否是注释
        if(c=='/')
        {
        	this.fstream.mark(0);
            c = this.fstream.read();
            if (-1 == c)
                return new Token(Kind.TOKEN_EOF, line);
        	if(c=='/')
        		{
        			c = this.fstream.read();
        			if (-1 == c)
        				return new Token(Kind.TOKEN_EOF, line); 
        			while(c!='\n')
        			{
        			   c = this.fstream.read();
        	           if (-1 == c)
        	             return new Token(Kind.TOKEN_EOF, line);
        			}
        			line++;
        			return new Token(Kind.TOKEN_ANNO,line-1);
        		//	return null;
        		}
        	else
        		this.fstream.reset();
        }
        
        //判断是否是数字
        while(c >= '0' && c <= '9')                     
        {
        	this.fstream.mark(0);
        	c = this.fstream.read();
            if (-1 == c)
                return new Token(Kind.TOKEN_EOF, line);
            r1 = (char) c;
            if(c >= '0' && c <= '9') 
            	str.append(r1);
        }
        if(str.length()>1 || (str.charAt(0)>= '0'&& str.charAt(0) <= '9'))                              //如果长度大于一，str中肯定有数字
        {
        	this.fstream.reset();
        	return new Token(Kind.TOKEN_NUM, line, str.toString());
        }
        
        this.fstream.mark(0);
        c = this.fstream.read();
        r1 = (char)c;                                   //读入的第二个字符
        if (-1 == c)
            return new Token(Kind.TOKEN_EOF, line);

        while(' ' != c && '\t' != c && c != '\n')
        {
        	char r2 = (char)c;
        	//尾随的是否是 ; , + - ....
        	if(searchSmallTable(String.valueOf(r2))==1)    
        	{
        		if(this.fstream.markSupported())
        		{
        			this.fstream.reset(); 
        		}
        		else
        		{
        			System.out.println("InputStream does not support reset !");
        		}
        		return new Token(Kind.TOKEN_ID,line,str.toString());
        	}
            str.append(r2);
            //是否是Token一枚
        	if(searchTable(str.toString())==1)             
        	{
        	   this.fstream.mark(0);
        	   c = this.fstream.read();
               if (-1 == c)
            	   return new Token(Kind.TOKEN_EOF, line);
               char c4 = (char) c;
               if ((c >='0' && c<='9')|| (c>='a' && c<='z') || (c>='A'&&c<='Z')||(c=='_'))
        	   {
        		   str.append(String.valueOf(c4));
        	   }
        	   else
        	   {
        		   this.fstream.reset();
                   return new Token(res2.get(str.toString()),line,res.get(str.toString()));
        	   }
        	 }
        	this.fstream.mark(0);
        	c = this.fstream.read();
        	if (-1 == c)
        	   return new Token(Kind.TOKEN_EOF, line);
        }
        if(c == '\n')
        {
        	line++;
            return new Token(Kind.TOKEN_ID,line-1,str.toString());
        }
        else 
        {
        	return new Token(Kind.TOKEN_ID,line,str.toString());
        }
    }
  }
  
 
  
  //查表接口
  public int searchTable(String str)
  {
	  if(res.get(str)!=null)     
	  {
		//System.out.println(res.get(str));
		  return 1;
	  }
	  else
		  return 0;
  }

  public int searchSmallTable(String str)
  {
	  if(res3.get(str)!=null)     
	  {
		 // System.out.println("we caught"+res3.get(str));
		  return 1;
	  }
	  else
		  return 0;
  }
  
  public Token nextToken()
  {
    Token t = null;

    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (dump)
      System.out.println(t.toString());
    return t;
  }
}

