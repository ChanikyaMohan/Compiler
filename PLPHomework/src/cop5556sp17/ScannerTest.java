package cop5556sp17;

import static cop5556sp17.Scanner.Kind;


import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

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
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = Kind.SEMI.getText();
		assertEquals(text.length(), token.length);
		//System.out.println(token.kind);
		//System.out.println(token.pos);
		//System.out.println(token.length);
		//System.out.println(token.getText());
		
		//System.out.println(token);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.SEMI, token2.kind);
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
	
//TODO  more tests
	
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		//TODO handle space after token
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void testIdentifier() throws IllegalCharException, IllegalNumberException{
		String input = "chandu -";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token = scanner.nextToken();
		Scanner.Token token1 = scanner.nextToken();
		
		//System.out.println(token.kind);
		//System.out.println(token.pos);
		//System.out.println(token.length);
		//System.out.println(token.getText());
		
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		
		//System.out.println(token1.kind);
		//System.out.println(token1.pos);
		//System.out.println(token1.length);
		//System.out.println(token1.getText());
		
		assertEquals(Kind.MINUS, token1.kind);
		assertEquals(0, token.pos);
		
	}
	
	@Test
	public void testIdentifierReserve() throws IllegalCharException, IllegalNumberException{
		String input = "integer * =";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		
		Scanner.Token token = scanner.nextToken();
		Scanner.Token token1 = scanner.nextToken();
		

		
		//System.out.println(token.kind);
		//System.out.println(token.pos);
		//System.out.println(token.length);
		//System.out.println(token.getText());
		
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		
		System.out.println(token1.kind);
		System.out.println(token1.pos);
		System.out.println(token1.length);
		System.out.println(token1.getText());
		
		assertEquals(Kind.DIV, token1.kind);
		assertEquals(8, token1.pos);

	}
	
	@Test
	public void testOtherOpsBreakCase() throws IllegalCharException, IllegalNumberException{
		
		//testing minus and arrow
		String input = "-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token = scanner.nextToken();		
		
		//System.out.println(token.kind);
		//System.out.println(token.pos);
		//System.out.println(token.length);
		//System.out.println(token.getText());
		
		assertEquals(Kind.MINUS, token.kind);
		assertEquals(0, token.pos);
		
		input = "-> -";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.ARROW,token2.kind);
		assertEquals(0,token2.pos);
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.MINUS,token3.kind);
		assertEquals(3,token3.pos);
		
		//testing not and not equal
		input = "! != !";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token4 = scanner.nextToken();		
		assertEquals(Kind.NOT, token4.kind);
		assertEquals(0, token4.pos);
		
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token5.kind);
		assertEquals(2, token5.pos);
		
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(Kind.NOT, token6.kind);
		assertEquals(5, token6.pos);
		
		//testing div and comment
		input = "/ /* this is comment */ /*dfdfsfd";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token7 = scanner.nextToken();		
		assertEquals(Kind.DIV, token7.kind);
		assertEquals(0, token7.pos);
		
		Scanner.Token token8 = scanner.nextToken();
		assertEquals(Kind.EOF, token8.kind);
		assertEquals(33, token8.pos);
		
		//testing BARARROW
		input = "|-> |-";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token9 = scanner.nextToken();		
		assertEquals(Kind.BARARROW, token9.kind);
		assertEquals(0, token9.pos);
		
		Scanner.Token token10 = scanner.nextToken();
		assertEquals(Kind.OR, token10.kind);
		assertEquals(4, token10.pos);
		
		Scanner.Token token11 = scanner.nextToken();
		assertEquals(Kind.MINUS, token11.kind);
		assertEquals(5, token11.pos);
		
		//testing LT,LE,ASSIGN
		input = "<- < <= <";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token15 = scanner.nextToken();		
		assertEquals(Kind.ASSIGN, token15.kind);
		assertEquals(0, token15.pos);
		
		Scanner.Token token16 = scanner.nextToken();
		assertEquals(Kind.LT, token16.kind);
		assertEquals(3, token16.pos);
		
		Scanner.Token token12 = scanner.nextToken();
		assertEquals(Kind.LE, token12.kind);
		assertEquals(5, token12.pos);
		
		Scanner.Token token13 = scanner.nextToken();
		assertEquals(Kind.LT, token13.kind);
		assertEquals(8, token13.pos);
		
		Scanner.Token token14 = scanner.nextToken();
		assertEquals(Kind.EOF, token14.kind);
		assertEquals(9, token14.pos);
		
		//testing GT,GW
		input = "> >= >";
		scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token17 = scanner.nextToken();		
		assertEquals(Kind.GT, token17.kind);
		assertEquals(0, token17.pos);
		
		Scanner.Token token18 = scanner.nextToken();
		assertEquals(Kind.GE, token18.kind);
		assertEquals(2, token18.pos);
		
		Scanner.Token token19 = scanner.nextToken();
		assertEquals(Kind.GT, token19.kind);
		assertEquals(5, token19.pos);
	}
	
	@Test
	public void testReserveWords() throws IllegalCharException, IllegalNumberException{
		//testing reserved words
		String[] names = new String[] {"KW_INTEGER","KW_BOOLEAN","KW_IMAGE","KW_URL","KW_FILE","KW_FRAME","KW_WHILE","KW_IF","OP_SLEEP","KW_SCREENHEIGHT","KW_SCREENWIDTH","OP_GRAY","OP_CONVOLVE","OP_BLUR","KW_SCALE","OP_WIDTH","OP_HEIGHT","KW_XLOC","KW_YLOC","KW_HIDE","KW_SHOW","KW_MOVE","KW_TRUE","KW_FALSE"};
		String input = " integer boolean image url file frame while if sleep screenheight screenwidth gray convolve blur scale width height xloc yloc hide show move true false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		Scanner.Token token20;
		int resflag;
		for(String s:names){
			resflag = 0;
			token20 = scanner.nextToken();
			if(s.equals(String.valueOf(token20.kind))){
				resflag = 1;	
			}
			assertEquals(1,resflag);
		}
	}
	
}
