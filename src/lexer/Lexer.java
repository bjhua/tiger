package lexer;

import java.io.InputStream;

import lexer.Token.Kind;
import util.Todo;

public class Lexer {
	String fname; // the input file name to be compiled
	InputStream fstream; // input stream for the above file

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
	}

	String[] words = new String[] { "boolean", "class", "else", "extends",
			"false", "if", "int", "length", "main", "new", "out", "println",
			"public", "return", "static", "String", "this", "true", "this",
			"while", "void" };

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c = this.fstream.read();
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, null);

		// skip all kinds of "blanks"
		while (' ' == c || '\t' == c || '\n' == c) {
			c = this.fstream.read();
		}
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, null);

		switch (c) {
		case '+':
			return new Token(Kind.TOKEN_ADD, null);
		case '&':
			if (this.fstream.read() == '&') {
				return new Token(Kind.TOKEN_AND, null);
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, null);
		case ',':
			return new Token(Kind.TOKEN_COMMER, null);
		case '.':
			return new Token(Kind.TOKEN_DOT, null);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, null);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, null);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, null);
		case '<':
			return new Token(Kind.TOKEN_LT, null);
		case '!':
			return new Token(Kind.TOKEN_NOT, null);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, null);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, null);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, null);
		case ';':
			return new Token(Kind.TOKEN_SEMI, null);
		case '-':
			return new Token(Kind.TOKEN_SUB, null);
		case '*':
			return new Token(Kind.TOKEN_TIMES, null);
		case '0':
			return new Token(Kind.TOKEN_NUM, null);
		default:
			String id;
			if ('a' <= c && 'z' >= c || 'A' <= c && 'Z' >= c || '_' == c) {
				id = Integer.toString(c);
				int cc = fstream.read();
				while (' ' != cc && '\t' != cc && '\n' != cc) {
					if (('a' > cc || 'z' < cc) && ('A' > cc || 'Z' < cc) && ('_' != cc) && ('0' > cc || '9' < cc)) {
						new Todo();
						return null;
					}
					id.concat(Integer.toString(cc));
					cc = fstream.read();
				}
				
				return new Token(Kind.TOKEN_ID,null,id);
			} else if('0' <= c && '9' >= c){
				
			}
			
			// Lab 1, exercise 2: supply missing code to
			// lex other kinds of tokens.
			// Hint: think carefully about the basic
			// data structure and algorithms. The code
			// is not that much and may be less than 50 lines. If you
			// find you are writing a lot of code, you
			// are on the wrong way.
			new Todo();
			return null;
		}
	}

	public Token nextToken() {
		Token t = null;

		try {
			t = this.nextTokenInternal();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (control.Control.lex)
			System.out.println(t.toString());
		return t;
	}
}
