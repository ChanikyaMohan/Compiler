package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	
	/**
	 * Kind enum
	 */
	public static enum State {
		START,IN_DIGIT,IN_IDENT,AFTER_MINUS,AFTER_EQ,AFTER_NOT,AFTER_DIV,COMMENT,AFTER_OR,AFTER_LESS,AFTER_GREAT;
	}
	
	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			String text = new String(chars.substring(pos, pos+length));
			return text;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			return null;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			int val = Integer.parseInt(chars.substring(pos, pos+length));
			return val;
		}
		
	}

	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();


	}


	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO comment
	    int length = chars.length();
	    State state = State.START;
	    int startPos = 0;
	    int ch;
	    while (pos < length) {
	        ch = pos < length ? chars.charAt(pos) : -1;
	        switch (state) {
	            case START: {
	                pos = skipWhiteSpace(pos);
	                ch = pos < length ? chars.charAt(pos) : -1;
	                startPos = pos;
	                switch (ch) {
	                    //case -1: {tokens.add(new Token(Kind.EOF, pos, 0)); pos++;}  break;
	                    case ';': {tokens.add(new Token(Kind.SEMI, startPos, 1));pos++;} break;
	                    case ',': {tokens.add(new Token(Kind.COMMA, startPos, 1));pos++;} break;
	                    case '(': {tokens.add(new Token(Kind.LPAREN, startPos, 1));pos++;} break;
	                    case ')': {tokens.add(new Token(Kind.RPAREN, startPos, 1));pos++;} break;
	                    case '{': {tokens.add(new Token(Kind.LBRACE, startPos, 1));pos++;} break;
	                    case '}': {tokens.add(new Token(Kind.RBRACE, startPos, 1));pos++;} break;
	                    case '&': {tokens.add(new Token(Kind.AND, startPos, 1));pos++;} break;
	                    case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1));pos++;} break;
	                    case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1));pos++;} break;
	                    case '%': {tokens.add(new Token(Kind.MOD, startPos, 1));pos++;} break;
	                    case '/': {state = State.AFTER_DIV;pos++;} break;
	                    case '-': {state = State.AFTER_MINUS;pos++;} break;
	                    case '=': {state = State.AFTER_EQ;pos++;}break;
	                    case '!': {state = State.AFTER_NOT;pos++;}break;
	                    case '|': {state = State.AFTER_OR;pos++;} break;
	                    case '<': {state = State.AFTER_LESS;pos++;} break;
	                    case '>': {state = State.AFTER_GREAT;pos++;} break;
	                    case '0': {tokens.add(new Token(Kind.INT_LIT,startPos, 1));pos++;}break;
	                    default: {
	                        if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;} 
	                        else if (Character.isJavaIdentifierStart(ch)) {
	                             state = State.IN_IDENT;pos++;
	                         } 
	                         else {throw new IllegalCharException(
	                                    "illegal char " +ch+" at pos "+pos);
	                         }
	                      }
	                } // switch (ch)
	            }  break;// case start end
	            case IN_DIGIT: {
	            	if(Character.isDigit(ch)){
	            		pos++;
	            	} else {
	            		try{
	            			Integer.parseInt(chars.substring(startPos, pos - startPos));
	            		}catch(Exception NumberFormatException){
	            			throw new IllegalNumberException(
	            					"Illegal num at" +startPos);
	            		}
	            		tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
	            		state = State.START;
	            	}
	            } break;
	            case IN_IDENT: {
	            	if (Character.isJavaIdentifierPart(ch)) {
	                    pos++;
	              } else {
	                      tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
	                      state = State.START;
	              }
	            }  break;
	            case AFTER_MINUS: {
	            	if(chars.charAt(pos) == '>'){
	            		tokens.add(new Token(Kind.ARROW, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else {
	            		tokens.add(new Token(Kind.MINUS, startPos, 1));pos++;
	            		state = State.START;
	            	}
	            } break;
	            case AFTER_EQ: {
	            	if(chars.charAt(pos) == '='){
	            		tokens.add(new Token(Kind.EQUAL, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else {
	            		// TODO implement throwing an error if we get only '='
	            		throw new IllegalCharException(
                                "illegal char " +chars.charAt(pos)+" at pos "+pos);
	            	}
	            } break;
	            case AFTER_NOT: {
	            	if(chars.charAt(pos) == '='){
	            		tokens.add(new Token(Kind.NOTEQUAL, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else {
	            		tokens.add(new Token(Kind.NOT, startPos, 1));pos++;
	            		state = State.START;
	            	}
	            } break;
	            case AFTER_DIV: {
	            	if(chars.charAt(pos) == '*'){
	            		state = State.COMMENT;pos++;
	            	} else {
	            		tokens.add(new Token(Kind.DIV, startPos, 1));pos++;
	            		state = State.START;
	            	}
	            } break;
	            case COMMENT: {
	            	if(chars.charAt(pos)  == '\n'){
	            		//TODO linenumber
	            		//lineNumber++;
	            	} else if(chars.charAt(pos) == '*' && chars.charAt(pos+1)=='/'){
	            		pos = pos + 2;
	            		state = State.START;
	            	} else {
	            		pos++;
	            	}
	            } break;
	            case AFTER_OR: {
	            	if(chars.charAt(pos) == '-' && chars.charAt(pos+1) == '>'){
	            		tokens.add(new Token(Kind.BARARROW, startPos, pos - startPos));pos=pos+2;
	            		state = State.START;
	            	} else {
	            		tokens.add(new Token(Kind.OR, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	}
	            } break;
	            case AFTER_LESS: {
	            	if(chars.charAt(pos) == '-'){
	            		tokens.add(new Token(Kind.ASSIGN, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else if(chars.charAt(pos) == '=') {
	            		tokens.add(new Token(Kind.LE, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else {
	            		tokens.add(new Token(Kind.LT, startPos, pos - startPos));
	            		state = State.START;
	            	}
	            } break;
	            case AFTER_GREAT: {
	            	if(chars.charAt(pos) == '='){
	            		tokens.add(new Token(Kind.GE, startPos, pos - startPos));pos++;
	            		state = State.START;
	            	} else {
	            		tokens.add(new Token(Kind.GT, startPos, pos - startPos));
	            		state = State.START;
	            	}
	            } break;
	            default:  assert false;
	        }// main - switch(state)
	    } // while
	    

		
		tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}



	public int skipWhiteSpace(int pos) {
		// TODO Auto-generated method stub
		char ch = chars.charAt(pos);
		while(Character.isWhitespace(ch)){
			pos++;
			ch = chars.charAt(pos);
		}
		return pos;
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum+1);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return null;
	}


}
