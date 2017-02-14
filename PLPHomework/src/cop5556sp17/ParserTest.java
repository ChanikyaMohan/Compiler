package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog1 integer a, boolean b, url i{}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgramErr() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {a,b}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
		
	}

	@Test
	public void testProgramErr1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {a|->b}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testProgramErr2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 a|->b}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testProgramNoErr2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {a|->b;}";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testProgramNoErr3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog integer i,url u,boolean b {integer i boolean b sleep a*c;}";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "a+b;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
		
		input = "a+b-3*4+z/2%r;";
		parser = new Parser(new Scanner(input).scan());
		parser.expression();
	}
	
	@Test
	public void testExpressionErr() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog {while(a,b){}}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testExpressionErr1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog {if(a+b-3,4+z/2%r){}}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	@Test
	public void testExpressionErr2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog {blur -> a; if(alb-3*4++z/2%r){} }";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog { blur -> a; c <- b+c; }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testAssignErr() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog { blur -> a; c <- +c; }";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testAssignErr1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog { blur -> a;  <- b+c; }";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testAssignErr2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog { blur -> a;  a <- ; }";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
}
