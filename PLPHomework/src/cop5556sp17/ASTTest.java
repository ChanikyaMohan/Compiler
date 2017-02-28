package cop5556sp17;

import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.*;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.*;


public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}

	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "identifier { integer a }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		Program program = (Program) ast;
		assertEquals(Block.class, program.getB().getClass());
		assertEquals(Dec.class, program.getB().getDecs().get(0).getClass());
	}
	
	@Test
	public void testempty() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.arg();
		Tuple t = (Tuple) ast;
		assertEquals(ast.getFirstToken().kind,Kind.EOF);
		assertEquals(t.getExprList().size(), 0);
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "identifier { integer a if(a==b){x <- b;} while(a>=d){a -> d;} sleep (a>b) + (c<d);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		Program program = (Program) ast;
		assertEquals(Block.class, program.getB().getClass());
		assertEquals(Dec.class, program.getB().getDecs().get(0).getClass());
		assertEquals(IfStatement.class, program.getB().getStatements().get(0).getClass());
		assertEquals(WhileStatement.class, program.getB().getStatements().get(1).getClass());	
		assertEquals(SleepStatement.class, program.getB().getStatements().get(2).getClass());	
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "identifier -> convolve((a + b)) -> width;";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(ast.getClass(), BinaryChain.class);
		BinaryChain bc = (BinaryChain) ast;
		assertEquals(bc.firstToken.kind, Kind.IDENT);
		assertEquals(bc.getE0().getClass(), BinaryChain.class);
		assertEquals(bc.getE1().getFirstToken().kind, Kind.OP_WIDTH);
	}
	
	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve ((a+b),true)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
	}
	
}