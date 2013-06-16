package lexer;

import java.io.InputStream;

import lexer.Token.Kind;

public class Lexer {
	String fname; // the input file name to be compiled
	InputStream fstream; // input stream for the above file
	Integer lineNum;

	public String getFileName() {
		return new String(fname.substring(fname.lastIndexOf('/') + 1));
	}

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
		lineNum = 1;
	}
	
	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c = this.fstream.read();
		String[] tokenStrings = { "boolean", "class", "else", "extends",
				"false", "if", "int", "length", "main", "new", "out",
				"println", "public", "return", "static", "String", "System",
				"this", "true", "void", "while" };
		Kind[] tokenKinds = { Kind.TOKEN_BOOLEAN, Kind.TOKEN_CLASS,
				Kind.TOKEN_ELSE, Kind.TOKEN_EXTENDS, Kind.TOKEN_FALSE,
				Kind.TOKEN_IF, Kind.TOKEN_INT, Kind.TOKEN_LENGTH,
				Kind.TOKEN_MAIN, Kind.TOKEN_NEW, Kind.TOKEN_OUT,
				Kind.TOKEN_PRINTLN, Kind.TOKEN_PUBLIC, Kind.TOKEN_RETURN,
				Kind.TOKEN_STATIC, Kind.TOKEN_STRING, Kind.TOKEN_SYSTEM,
				Kind.TOKEN_THIS, Kind.TOKEN_TRUE, Kind.TOKEN_VOID,
				Kind.TOKEN_WHILE };
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, lineNum);

		// skip all kinds of "blanks"
		while (' ' == c || '\t' == c || '\n' == c || '\r' == c) {
			if ('\n' == c) {
				lineNum++;
			}
			c = this.fstream.read();
		}
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, lineNum);

		if ('/' == c) {
			c = this.fstream.read();
			switch (c) {
			case '/':
				while (c != '\n') {
					c = fstream.read();
				}
				lineNum++;
				return this.nextTokenInternal();
			case '*':
				int depth = 1;
				while (depth > 0) {
					c = this.fstream.read();
					if ('\n' == c) {
						lineNum++;
					}
					if ('*' == c) {
						c = this.fstream.read();
						if ('/' == c) {
							depth--;
						}
					} else if ('/' == c) {
						c = this.fstream.read();
						if ('*' == c) {
							depth++;
						}
					}
				}
				return this.nextTokenInternal();
			}
			return null;
		}

		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			StringBuffer buffer = new StringBuffer();
			do {
				buffer.append((char) c);
				this.fstream.mark(1);
				c = this.fstream.read();
			} while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || (c == '_'));
			this.fstream.reset();
			String str = buffer.toString();
			for (int i = 0; i < tokenStrings.length; i++) {
				if (str.equals(tokenStrings[i])) {
					return new Token(tokenKinds[i], lineNum);
				}
			}
			return new Token(Kind.TOKEN_ID, lineNum, str);
		}

		if (c >= '0' && c <= '9') {
			Integer val = 0;
			do {
				val = val * 10 + c - '0';
				this.fstream.mark(1);
				c = this.fstream.read();
			} while (c >= '0' && c <= '9');
			this.fstream.reset();
			return new Token(Kind.TOKEN_NUM, lineNum, val.toString());
		}

		switch (c) {
		case '+':
			return new Token(Kind.TOKEN_ADD, lineNum);
		case '&':
			if (this.fstream.read() == '&') {
				return new Token(Kind.TOKEN_AND, lineNum);
			} else {
				return null;
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, lineNum);
		case ',':
			return new Token(Kind.TOKEN_COMMER, lineNum);
		case '.':
			return new Token(Kind.TOKEN_DOT, lineNum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, lineNum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, lineNum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, lineNum);
		case '<':
			return new Token(Kind.TOKEN_LT, lineNum);
		case '!':
			return new Token(Kind.TOKEN_NOT, lineNum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, lineNum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, lineNum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, lineNum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, lineNum);
		case '-':
			return new Token(Kind.TOKEN_SUB, lineNum);
		case '*':
			return new Token(Kind.TOKEN_TIMES, lineNum);
		default:
			// Lab 1, exercise 2: supply missing code to
			// lex other kinds of tokens.
			// Hint: think carefully about the basic
			// data structure and algorithms. The code
			// is not that much and may be less than 50 lines. If you
			// find you are writing a lot of code, you
			// are on the wrong way.
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
