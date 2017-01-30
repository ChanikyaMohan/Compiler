package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();	
		
		Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		//System.out.println(token.intVal());
		//assertEquals(99999999999999999, token.intVal());
	}

	//TODO  more tests
	@Test
	public void testWhitespace1() throws IllegalCharException, IllegalNumberException {
		String input = "    \n \r   abc";
		Scanner scanner = new Scanner(input);
		
		scanner.scan();
		Token t;
		while((t = scanner.nextToken()) != null){
			System.out.print(t.getText() + "   --->type: " + t.kind + " ---->    position: " + t.getLinePos());
			System.out.println();	
		}
	}
	
	
	
	@Test
	public void testMix4() throws IllegalCharException, IllegalNumberException{
		String input = "     123\n"
				+ "123 0 0000\n"
				+ "   abcd cnda 123\n"
				+ "abc abc !===\n"
				+ "abc";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();	
		Token t;
		while((t = scanner.nextToken()) != null){
			System.out.print(t.getText() + "   --->type: " + t.kind + " ---->    position: " + t.getLinePos());
			System.out.println();	
		}
	}
	
	
	@Test
	public void testMix3() throws IllegalCharException, IllegalNumberException{
		String input = ";()(;\n"
				+ "}{+)!(\n"
				+ "!!!=!=!,\n"
				+ "--->->-\n"
				+ "|;|--->->-|->\n"
				+ "<<<=>>>=>< ->-->\n"
				+ "123()+4+54321\n"
				+ "a+b;a23a4\n"
				+ "ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image "
				+ "abc!!d"
				+ "          ;     "
				+ "\n\n\r;"
				+ "a\nbc! !\nd"
				+ "/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/"
				+ "/* * ** */\nabc "
				+ "show\r\n hide \n move \n file (3,5) -< <- <+ <= <\n-<--"
				+ "***%&/****/"
				+ "/*dsafdsafdaswfasdfasd";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();	
		Token t;
		while((t = scanner.nextToken()) != null){
			System.out.print(t.getText() + "   --->type: " + t.kind + " ---->    position: " + t.getLinePos());
			System.out.println();	
		}
	}
	
	@Test
	public void testException() throws IllegalCharException, IllegalNumberException {
		String input = "abc def/n345 #abc";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testNewORMINUS() throws IllegalCharException, IllegalNumberException{
		String input = "|-i |-> |-* |-- |--|";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();	
		Token t;
		while((t = scanner.nextToken()) != null){
			//System.out.print(t.getText() + "    position: " + t.getLinePos());
			System.out.print(t.getText()+" ");
			System.out.print(t.pos+" ");
			System.out.println(t.length+" ");
		}
	}
	
	@Test
	public void testError1() throws IllegalCharException, IllegalNumberException{
		String input = "/*alex*sadaf*/*/";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();	
		
		Token token = scanner.nextToken();
		assertEquals(TIMES, token.kind);

		
		Token token1 = scanner.nextToken();
		assertEquals(DIV, token1.kind);

	}
	
	@Test
	public void testError2() throws IllegalCharException, IllegalNumberException{
		String input = "1012  2003 10000 0 012";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalNumberException.class);
		scanner.scan();	
		
		Token t;
		while((t = scanner.nextToken()) != null){
			System.out.print(t.getText() + "    position: " + t.getLinePos());
			System.out.println();	
		}
	}
	
	@Test
	public void testError3() throws IllegalCharException, IllegalNumberException{
		String input = "0&$abc";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();	
		
		Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		
		Token token1 = scanner.nextToken();
		assertEquals(AND, token1.kind);
		assertEquals(1, token1.pos);
		
		Token token2 = scanner.nextToken();
		assertEquals(IDENT, token2.kind);
		assertEquals(2, token2.pos);
		System.out.println(token2.getText());
	}
	
	@Test
	public void testMix1() throws IllegalCharException, IllegalNumberException{
		String input = "url while(11){if->ifa|->show(0)<-/*this is comment*/};\n 2 - 1 == 1";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token t;
		while((t = scanner.nextToken()) != null){
			
				System.out.print(t.getText() + "    position: " + t.getLinePos());
				System.out.println();
			
		}
		
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(KW_URL, token.kind);
		assertEquals(0, token.pos);
		
		Token token1 = scanner.nextToken();
		assertEquals(KW_WHILE, token1.kind);
		assertEquals(4, token1.pos);
		
		Token token2 = scanner.nextToken();
		assertEquals(LPAREN, token2.kind);
		assertEquals(9, token2.pos);
		
		Token token3 = scanner.nextToken();
		assertEquals(INT_LIT, token3.kind);
		assertEquals(10, token3.pos);
		
		Token token4 = scanner.nextToken();
		assertEquals(RPAREN, token4.kind);
		assertEquals(12, token4.pos);
		
		Token token5 = scanner.nextToken();
		assertEquals(LBRACE, token5.kind);
		assertEquals(13, token5.pos);
		
		Token token6 = scanner.nextToken();
		assertEquals(KW_IF, token6.kind);
		assertEquals(14, token6.pos);
		
		Token token7 = scanner.nextToken();
		assertEquals(ARROW, token7.kind);
		assertEquals(16, token7.pos);
		
		Token token8 = scanner.nextToken();
		assertEquals(IDENT, token8.kind);
		assertEquals(18, token8.pos);
	}
	
	@Test
	public void testMix2() throws IllegalCharException, IllegalNumberException{

//		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
//		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
//		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
//		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
//		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
//		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
//		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
//		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
//		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
//		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
//		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
//		KW_SCALE("scale"), EOF("eof");
		
		String input = ";,;,-><<=>===\n"
				+ "_abc adc123 $123 _123 0 123 123456 integer boolean \n"
				+ "image url file frame \n"
				+ "while if true false \n"
				+ ";,(){\n"
				+ "}->|->|&\n"
				+ "/*this is a comment 123* / ->*/ \n"
				+ "==!=<><=>= \n"
				+ "+-*/%!\n"
				+ "<-blur gray convolve\n"
				+ "screenheight screenwidth \n"
				+ "width height xloc yloc \n"
				+ "hide show move sleep\n"
				+ "scale eof";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token t;
		while((t = scanner.nextToken()) != null){
			
				System.out.print(t.getText() + "   ->>type: " + t.kind + "   -->    position: " + t.getLinePos());
				System.out.println();
			
		}
	}
	
	@Test
	public void testEdgeCase1() throws IllegalCharException, IllegalNumberException{
		for(Kind k: Kind.values()){
		
			if(k == Kind.IDENT || k == Kind.INT_LIT)
				continue;
			else{
				String input = k.getText();
				Scanner scanner = new Scanner(input);
				scanner.scan();
				
				Token token = scanner.nextToken();
				assertEquals(k, token.kind);
				assertEquals(0, token.pos);
				
				System.out.println(token.getText());
			}
		}
	}
	
	@Test
	public void testspecialcase() throws IllegalCharException, IllegalNumberException{
		
			String input = "abc_012";
			Scanner scanner = new Scanner(input);
			scanner.scan();
				
			Token token = scanner.nextToken();
			assertEquals(IDENT, token.kind);
			assertEquals(0, token.pos);
			assertEquals("abc_012", token.getText());
	}
	
	@Test
	public void testIdent1()  throws IllegalCharException, IllegalNumberException{
		String input = "123 abc123 move a2 blur";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(123, token.intVal());
		
		Token token1 = scanner.nextToken();
		assertEquals(IDENT, token1.kind);
		assertEquals(4, token1.pos);
		assertEquals("abc123", token1.getText());
		
		Token token2 = scanner.nextToken();
		assertEquals(KW_MOVE, token2.kind);
		assertEquals(11, token2.pos);
		assertEquals("move", token2.getText());
		
		Token token3 = scanner.nextToken();
		assertEquals(IDENT, token3.kind);
		assertEquals(16, token3.pos);
		assertEquals("a2", token3.getText());
		
		Token token4 = scanner.nextToken();
		assertEquals(OP_BLUR, token4.kind);
		assertEquals(19, token4.pos);
		assertEquals("blur", token4.getText());
	}
	
	@Test
	public void testNumber1() throws IllegalCharException, IllegalNumberException{
		String input = "5678 - 1234 == 0+ 0123 0";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalNumberException.class);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(5678, token.intVal());
		
		Token token1 = scanner.nextToken();
		assertEquals(MINUS, token1.kind);
		assertEquals(5, token1.pos);
		
		Token token2 = scanner.nextToken();
		assertEquals(INT_LIT, token2.kind);
		assertEquals(7, token2.pos);
		assertEquals(1234, token2.intVal());
		
		Token token3 = scanner.nextToken();
		assertEquals(EQUAL, token3.kind);
		assertEquals(12, token3.pos);
		
		Token token4 = scanner.nextToken();
		assertEquals(INT_LIT, token4.kind);
		assertEquals(15, token4.pos);
		assertEquals(0, token4.intVal());
	}
	
	
	@Test
	public void testComment() throws IllegalCharException, IllegalNumberException{
		String input = "< /* abc 123 blur  * + */ <-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(LT, token.kind);
		assertEquals(0, token.pos);
		
		Token token1 = scanner.nextToken();
		assertEquals(ASSIGN, token1.kind);
		assertEquals(26, token1.pos);
	}
	
	@Test
	public void testLE_GE_ARROW()throws IllegalCharException, IllegalNumberException{
		String input = "<  <- <= > >= -  -> | |->  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(LT, token.kind);
		assertEquals(0, token.pos);
		
		Token token1 = scanner.nextToken();
		assertEquals(ASSIGN, token1.kind);
		assertEquals(3, token1.pos);
		
		Token token2 = scanner.nextToken();
		assertEquals(LE, token2.kind);
		assertEquals(6, token2.pos);
		
		Token token3 = scanner.nextToken();
		assertEquals(GT, token3.kind);
		assertEquals(9, token3.pos);
		
		Token token4 = scanner.nextToken();
		assertEquals(GE, token4.kind);
		assertEquals(11, token4.pos);
		
		Token token5 = scanner.nextToken();
		assertEquals(MINUS, token5.kind);
		assertEquals(14, token5.pos);
		
		Token token6 = scanner.nextToken();
		assertEquals(ARROW, token6.kind);
		assertEquals(17, token6.pos);
		
		Token token8 = scanner.nextToken();
		assertEquals(OR, token8.kind);
		assertEquals(20, token8.pos);
		
		Token token9 = scanner.nextToken();
		assertEquals(BARARROW, token9.kind);
		assertEquals(22, token9.pos);
	}
	
	@Test
	public void testEq() throws IllegalCharException, IllegalNumberException{
		String input = " ==  =a  ";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(EQUAL, token.kind);
		assertEquals(1, token.pos);
	}
	

	@Test
	public void testNotandNotEq() throws IllegalCharException, IllegalNumberException{
		String input = " != !   ";
		Scanner scanner = new Scanner(input);
		int len = input.length();
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(NOTEQUAL, token.kind);
		assertEquals(1, token.pos);
		

		Token token1 = scanner.nextToken();
		assertEquals(NOT, token1.kind);
		assertEquals(4, token1.pos);
		
//		Token token2 = scanner.nextToken();
//		assertEquals(NOT, token2.kind);
//		assertEquals(8, token2.pos);
		
		Token token3 = scanner.nextToken();
		assertEquals(EOF, token3.kind);
		assertEquals(len, token3.pos);
	}
	
	@Test
	public void testgetTextforCharacters() throws IllegalCharException, IllegalNumberException{
		String input = "|-> <- == + - * / % () {} ! != | & == */";
		//thrown.expect(IllegalCharException.class);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(BARARROW, token.kind);
		assertEquals(0, token.pos);
		assertEquals("|->", token.getText());
		
		Token token1 = scanner.nextToken();
		assertEquals("<-", token1.getText());
		
		Token token2 = scanner.nextToken();
		assertEquals("==", token2.getText());
		
		Token token3 = scanner.nextToken();
		assertEquals("+", token3.getText());
		
		Token token4 = scanner.nextToken();
		assertEquals("-", token4.getText());
		
		Token token5 = scanner.nextToken();
		assertEquals("*", token5.getText());
	}
	
	@Test
	public void testAllWhiteSpace() throws IllegalCharException, IllegalNumberException{
		String input = "        ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(EOF, token.kind);
		assertEquals(8, token.pos);
	}
	
	@Test
	public void testAllNotNestedSimbol() throws IllegalCharException, IllegalNumberException{
		String input = "* ,  +%;(){}&       ";
		int len = input.length();
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Token token = scanner.nextToken();
		assertEquals(TIMES, token.kind);
		assertEquals(0, token.pos);
		
		Token token1 = scanner.nextToken();
		assertEquals(COMMA, token1.kind);
		assertEquals(2, token1.pos);
		
		Token token2 = scanner.nextToken();
		assertEquals(PLUS, token2.kind);
		assertEquals(5, token2.pos);
		
		Token token3 = scanner.nextToken();
		assertEquals(MOD, token3.kind);
		assertEquals(6, token3.pos);
		
		Token token4 = scanner.nextToken();
		assertEquals(SEMI, token4.kind);
		assertEquals(7, token4.pos);
		
		Token token5 = scanner.nextToken();
		assertEquals(LPAREN, token5.kind);
		assertEquals(8, token5.pos);
		
		Token token6 = scanner.nextToken();
		assertEquals(RPAREN, token6.kind);
		assertEquals(9, token6.pos);
		
		Token token7 = scanner.nextToken();
		assertEquals(LBRACE, token7.kind);
		assertEquals(10, token7.pos);
		
		Token token8 = scanner.nextToken();
		assertEquals(RBRACE, token8.kind);
		assertEquals(11, token8.pos);
		
		Token token9 = scanner.nextToken();
		assertEquals(AND, token9.kind);
		assertEquals(12, token9.pos);
		
		Token token10 = scanner.nextToken();
		assertEquals(EOF, token10.kind);
		assertEquals(len, token10.pos);
	}
	
}
