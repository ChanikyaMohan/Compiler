package cop5556sp17;

import cop5556sp17.Scanner.Kind;

import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

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
	ASTNode parse() throws SyntaxException {
		ASTNode node = program();
		matchEOF();
		return node;
	}
	
	Program program() throws SyntaxException {
		//Implemented
		ArrayList<ParamDec> paramDecList = new ArrayList<>();
		Block block;
		Token first = t;
		
		match(IDENT);
		if (t.isKind(LBRACE)){
			block = block();
		}
		else if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			paramDecList.add(paramDec());
			while(t.isKind(COMMA)){
				consume();
				//System.out.println("Comma consumed");
				paramDecList.add(paramDec()); 
			}
			block = block();
		} else {
			throw new SyntaxException("saw: " +t.kind +"expected: " +"Identifier or Paramdec");
		}
		return new Program(first, paramDecList,block);
	}
	
	Expression expression() throws SyntaxException {
		//Implemented this
		Token first = t, operator;
		Expression exp,exp1;
		exp = term();
		while (t.isKind(LT ) || t.isKind(LE) || t.isKind(GT)|| t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) 
		{	
			operator = t;
			consume();
			exp1 = term();
			exp = new BinaryExpression(first, exp, operator, exp1); 
		}
		return exp;
	}
	
	Expression term() throws SyntaxException {
		//Implemented
		Token first = t,operator;
		Expression exp,exp1;
		exp = elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR))
		 {
			operator = t;
			consume(); 
			exp1 = elem();
			exp = new BinaryExpression(first,exp,operator,exp1);
		}
		return exp;
	}

	Expression elem() throws SyntaxException {
		//Implemented
		Token first = t,operator;
		Expression exp,exp1;
		exp = factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD))
		 {
			operator = t;
			consume(); 
			exp1 = factor();
			exp = new BinaryExpression(first, exp, operator, exp1);
		}
		return exp;
	}

	Expression factor() throws SyntaxException {
		Token first = t;
			
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
			return new IdentExpression(first);
		}
		case INT_LIT:{
			consume();
			return new IntLitExpression(first);
		}
		case KW_TRUE: case KW_FALSE:{
			consume();
			return new BooleanLitExpression(first);
		}
		case KW_SCREENWIDTH: case KW_SCREENHEIGHT:{
			consume();
			return new ConstantExpression(first);
		}
		case LPAREN: {
			Expression exp;
			consume();
			exp = expression();
			match(RPAREN);
			return exp;
		}
		default:
			throw new SyntaxException("illegal factor");
		}
	}

	Block block() throws SyntaxException {
		//Implemented
		Token first = t;
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		
		if(t.isKind(LBRACE)){
			consume();
			while(!t.isKind(RBRACE)){
				if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
					decList.add(dec()); 
				} else {
					statementList.add(statement());
				}
			}
			match(RBRACE);
		} else {
			throw new SyntaxException("illegal token saw: " +t.kind +"expected: " +"block");
		}
		return new Block(first,decList,statementList);
	}

	ParamDec paramDec() throws SyntaxException {
		//Implemented
		Token first = t,next;
		if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			consume();
			
		} else {
			throw new SyntaxException("illegal token saw: " +t.kind +"expected: " +"param dec");
		}
		next = t;
		match(IDENT);
		return new ParamDec(first,next);
	}

	Dec dec() throws SyntaxException {
		//Implemented
		Token first = t,next;
		if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)){
			consume();
		} else {
			throw new SyntaxException("illegal factor");
		}
		next = t;
		match(IDENT);
		return new Dec(first,next);
	}

	Statement statement() throws SyntaxException {
		//Implemented
		Token first = t;
		Statement stat;
		if(t.isKind(OP_SLEEP)){
			match(OP_SLEEP);
			stat = new SleepStatement(first,expression());
			match(SEMI);
		} else if(t.isKind(KW_WHILE)){
			stat = whileStatement();
		} else if(t.isKind(KW_IF)){
			stat = ifStatement();
		} else if(t.isKind(IDENT)){
			if (scanner.peek().kind.toString() == "ASSIGN"){
				stat = assign();
			} else {
				stat = chain();
			}
			//System.out.println("Semi matched");
			match(SEMI);
		} else {
			if (t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) ||t.isKind(KW_XLOC) || t.isKind(KW_YLOC) || t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)){
				stat = chain();
				//System.out.println("Semi matched");
				match(SEMI);
			}
			else{
				throw new SyntaxException("illegal token"+t.kind+"expected: " +" a statements");
			}
		}
		return stat;
	}
	
	IfStatement ifStatement() throws SyntaxException {
		Token first =t;
		Expression exp;
		Block b;
		
		match(KW_IF);
		if(t.isKind(LPAREN)){
			consume();
			exp = expression();
			match(RPAREN);
			b = block();
		} else {
			throw new SyntaxException("illegal token"+t.kind+"expected: " +" if statement");
		}
		return new IfStatement(first,exp,b);
	}
	
	AssignmentStatement assign() throws SyntaxException {
		IdentLValue var;
		Expression exp;
		Token first= t;
		var = new IdentLValue(t);
		match(IDENT);
		match(ASSIGN);
		exp = expression();
		return new AssignmentStatement(first, var, exp);
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		Token first = t;
		Expression exp;
		Block bl;
		
		match(KW_WHILE);
		if(t.isKind(LPAREN)){
			consume();
			exp = expression();
			match(RPAREN);
			bl = block();
		} else {
			throw new SyntaxException("illegal token"+t.kind+"expected: " +" while statement");
		}
		return new WhileStatement(first, exp, bl);
	}
	
	Chain chain() throws SyntaxException {
		//Implemented
		Token first = t,op;
		Chain c;
		ChainElem c1;
		c = chainElem();
		op = t;
		arrowOp();
		c1 = chainElem();
		c = new BinaryChain(first,c,op,c1);
		while(t.isKind(ARROW) || t.isKind(BARARROW)){
			op = t;
			arrowOp();
			c1 = chainElem();
			c = new BinaryChain(first,c,op,c1);
		}
		return c;
	}
	
	void arrowOp() throws SyntaxException {
		match(ARROW, BARARROW);
	}
	
	ChainElem chainElem() throws SyntaxException {
		//Implemented
		//System.out.println("chain elem");
		Token first = t;
		ChainElem chel;
		
		if(t.isKind(IDENT)){
			//System.out.println("identifier consumed");
			consume();
			chel = new IdentChain(first);
		} else if(t.isKind(OP_BLUR ) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)){
			consume();
			chel = new FilterOpChain(first, arg());
		} else if(t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC)){
			consume();
			chel = new FrameOpChain(first,arg());
		} else if(t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)){
			consume();
			chel = new ImageOpChain(first,arg());
		} else {
			throw new SyntaxException("illegal token"+t.kind+"expected: " +" chain element");
		}
		return chel;
	}

	Tuple arg() throws SyntaxException {
		//Implemented
		Token first = t;
		ArrayList<Expression> expList = new ArrayList<Expression>();
		if(t.isKind(LPAREN)){
			consume();
			expList.add(expression());
			while(t.isKind(COMMA)){
				consume();
				expList.add(expression());
			}
			match(RPAREN);
		}
		return new Tuple(first,expList);
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
		// Implemented. Optional but handy
		for (Kind k:kinds){
			if(t.isKind(k)){
				return consume();
			}
		}
		throw new SyntaxException("saw: " + t.kind + "expected: " );
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
