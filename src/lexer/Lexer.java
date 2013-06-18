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
			"public", "return", "static", "String", "Sysyem", "this", "true",
			"void", "while" };
	Kind[] kinds = new Kind[] { Kind.TOKEN_BOOLEAN, Kind.TOKEN_CLASS,
			Kind.TOKEN_ELSE, Kind.TOKEN_EXTENDS, Kind.TOKEN_FALSE,
			Kind.TOKEN_IF, Kind.TOKEN_INT, Kind.TOKEN_LENGTH, Kind.TOKEN_MAIN,
			Kind.TOKEN_NEW, Kind.TOKEN_OUT, Kind.TOKEN_PRINTLN,
			Kind.TOKEN_PUBLIC, Kind.TOKEN_RETURN, Kind.TOKEN_STATIC,
			Kind.TOKEN_STRING, Kind.TOKEN_SYSTEM, Kind.TOKEN_THIS,
			Kind.TOKEN_TRUE, Kind.TOKEN_VOID, Kind.TOKEN_WHILE };
	int tokenval;
	int lineno = 1;
	char lexbuf[];

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	// The value for "lineNum" is now "null",
	// you should modify this to an appropriate
	// line number for the "EOF" token.
	private Token nextTokenInternal() throws Exception {
		int c;
		c = this.fstream.read();
		while (true) {
			// skip all kinds of "blanks"
			while (' ' == c || '\t' == c || '\n' == c) {
				if ('\n' == c) {
					lineno++;
				}
				c = this.fstream.read();
			}
			if (-1 == c)
				return new Token(Kind.TOKEN_EOF, null);
			if (isdigit(c)) {
				tokenval = c - '0';
				c = this.fstream.read();
				while (isdigit(c)) {
					tokenval = tokenval * 10 + c - '0';
					c = this.fstream.read();
				}
				return new Token(Kind.TOKEN_NUM, lineno,
						Integer.toString(tokenval));
			}
			if (isalpha(c)) {
				int b = 0;
				while (isalpha(c) || isdigit(c)) {
					lexbuf[b] = (char) c;
					c = this.fstream.read();
					b++;
				}
				if (' ' == c || '\t' == c || '\n' == c) {
					if ('\n' == c) {
						lineno++;
					}
					lexbuf[b] = '\0';
					int p = lookup(String.valueOf(lexbuf));
					if (-1 != p) {
						return new Token(kinds[p], lineno - 1);
					} else {
						return new Token(Kind.TOKEN_ID, lineno - 1);
					}
				} else {
					new Todo();
					return null;
				}
			}
			switch (c) {
			case '+':
				return new Token(Kind.TOKEN_ADD, lineno);
			case '&':
				if (this.fstream.read() == '&') {
					return new Token(Kind.TOKEN_AND, lineno);
				}
			case '=':
				return new Token(Kind.TOKEN_ASSIGN, lineno);
			case ',':
				return new Token(Kind.TOKEN_COMMER, lineno);
			case '.':
				return new Token(Kind.TOKEN_DOT, lineno);
			case '{':
				return new Token(Kind.TOKEN_LBRACE, lineno);
			case '[':
				return new Token(Kind.TOKEN_LBRACK, lineno);
			case '(':
				return new Token(Kind.TOKEN_LPAREN, lineno);
			case '<':
				return new Token(Kind.TOKEN_LT, lineno);
			case '!':
				return new Token(Kind.TOKEN_NOT, lineno);
			case '}':
				return new Token(Kind.TOKEN_RBRACE, lineno);
			case ']':
				return new Token(Kind.TOKEN_RBRACK, lineno);
			case ')':
				return new Token(Kind.TOKEN_RPAREN, lineno);
			case ';':
				return new Token(Kind.TOKEN_SEMI, lineno);
			case '-':
				return new Token(Kind.TOKEN_SUB, lineno);
			case '*':
				return new Token(Kind.TOKEN_TIMES, lineno);
			default:
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

	}

	public boolean isdigit(int c) {
		return c - '0' >= 0 && c - '9' <= 0 ? true : false;
	}

	public boolean isalpha(int c) {
		return c - 'a' >= 0 && c - 'z' <= 0 || c - 'A' >= 0 && c - 'Z' <= 0
				|| c == '_' ? true : false;
	}

	public int lookup(String lexbuf) {
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals(lexbuf)) {
				return i;
			}
		}
		return -1;
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
