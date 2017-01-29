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
		scanner.scan();
		
		Scanner.Token token = scanner.nextToken();
		Scanner.Token token1 = scanner.nextToken();
		Scanner.Token token2 = scanner.nextToken();
		
		thrown.expect(IllegalCharException.class);
		
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
		
		System.out.println(token2.kind);
		System.out.println(token2.pos);
		System.out.println(token2.length);
		System.out.println(token2.getText());
		
		assertEquals(Kind.KW_INTEGER, token2.kind);
		assertEquals(10, token2.pos);
	}
	
}
