package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}
	
	void program() throws SyntaxException {
		//TODO
		System.out.println("program");
		match(IDENT);
		if (t.isKind(LBRACE)){
			block();
		}
		else if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			paramDec();
			while(t.isKind(COMMA)){
				consume();
				System.out.println("Comma consumed");
				paramDec();
			}
			block();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("saw: " +t.kind +"expected: " +"Identifier or Paramdec");
		}
	}
	
	void expression() throws SyntaxException {
		//TODO
		System.out.println("Expression");
		term();
		while (t.isKind(LT ) || t.isKind(LE) || t.isKind(GT)|| t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) 
		{
			consume();
			term();
		}
	}
	
	void term() throws SyntaxException {
		//TODO
		System.out.println("term");
		elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR))
		 {
			consume(); 
			elem();
		}
	}

	void elem() throws SyntaxException {
		//TODO
		System.out.println("elem");
		factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD))
		 {
			consume(); 
			factor();
		}
	}

	void factor() throws SyntaxException {
		System.out.println("factor");
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: case INT_LIT: case KW_TRUE: case KW_FALSE: 	case KW_SCREENWIDTH: case KW_SCREENHEIGHT:{
			consume();
		} break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		} break;
		default:
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}

	void block() throws SyntaxException {
		//TODO
		System.out.println("block");
		if(t.isKind(LBRACE)){
			consume();
			while(!t.isKind(RBRACE)){
				if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
					dec(); 
				} else {
					statement();
				}
			}
			match(RBRACE);
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}

	void paramDec() throws SyntaxException {
		//TODO
		System.out.println("paramdec");
		if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			consume();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		match(IDENT);
	}

	void dec() throws SyntaxException {
		//TODO
		System.out.println("dec");
		if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
			consume();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		match(IDENT);
	}

	void statement() throws SyntaxException {
		//TODO
		System.out.println("statement");
		if(t.isKind(OP_SLEEP)){
			match(OP_SLEEP);
			expression();
			match(SEMI);
		} else if(t.isKind(KW_WHILE)){
			whileStatement();
		} else if(t.isKind(KW_IF)){
			ifStatement();
		} else if(t.isKind(IDENT)){
			if (scanner.peek().isKind(ASSIGN)){
				assign();
			} else {
				chain();
			}
			System.out.println("Semi matched");
			match(SEMI);
		} else {
			if (t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) ||t.isKind(KW_XLOC) || t.isKind(KW_YLOC) || t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)){
				chain();
				System.out.println("Semi matched");
				match(SEMI);
			}
			else{
				throw new SyntaxException("illegal statement"+t.kind);
			}
		}
	}
	
	void ifStatement() throws SyntaxException {
		System.out.println("ifstatement");
		match(KW_IF);
		if(t.isKind(LPAREN)){
			consume();
			expression();
			match(RPAREN);
			block();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}
	
	void assign() throws SyntaxException {
		System.out.println("assign");
		match(IDENT);
		match(ASSIGN);
		expression();
	}
	
	void whileStatement() throws SyntaxException {
		System.out.println("whilestaement");
		match(KW_WHILE);
		if(t.isKind(LPAREN)){
			consume();
			expression();
			match(RPAREN);
			block();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}	
	}
	
	void chain() throws SyntaxException {
		//TODO
		System.out.println("chain");
		chainElem();
		arrowOp();
		chainElem();
		while(t.isKind(ARROW) || t.isKind(BARARROW)){
			arrowOp();
			chainElem();
		}
	}
	
	void arrowOp() throws SyntaxException {
		System.out.println("arrowop");
		match(ARROW, BARARROW);
	}
	
	void chainElem() throws SyntaxException {
		//TODO
		System.out.println("chain elem");
		if(t.isKind(IDENT)){
			System.out.println("identifier consumed");
			consume();	
		} else if(t.isKind(OP_BLUR ) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)){
			consume();
			arg();
		} else if(t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC)){
			consume();
			arg();
		} else if(t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)){
			consume();
			arg();
		} else {
			//TODO you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}

	void arg() throws SyntaxException {
		//TODO
		System.out.println("arg");
		if(t.isKind(LPAREN)){
			consume();
			expression();
			while(t.isKind(COMMA)){
				consume();
				expression();
			}
			match(RPAREN);
		}
		// don't throw error here
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		System.out.println("EOF match");
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for (Kind k:kinds){
			if(t.isKind(k)){
				return consume();
			}
		}
		throw new SyntaxException("saw: " + t.kind + "expected: " + kinds.toString());
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
