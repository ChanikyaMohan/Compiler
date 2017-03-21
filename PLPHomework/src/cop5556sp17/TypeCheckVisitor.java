package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

import cop5556sp17.AST.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first = binaryChain.getFirstToken();
		Chain chain = binaryChain.getE0();
		chain.visit(this, arg);
		Token t = binaryChain.getArrow();
		ChainElem celem = binaryChain.getE1();
		celem.visit(this, arg);
		if (t.isKind(ARROW)){
			if (chain.type.equals(URL) && 
				celem.type.equals(IMAGE)){
				binaryChain.type = IMAGE;
			} else if (chain.type.equals(FILE) &&
					celem.type.equals(IMAGE)){
				binaryChain.type = IMAGE;
			} else if (chain.type.equals(FRAME) && 
					(first.isKind(KW_XLOC)||
					 first.isKind(KW_YLOC)) &&
					(celem.getFirstToken().isKind(KW_SHOW)||
					 celem.getFirstToken().isKind(KW_HIDE)||
					 celem.getFirstToken().isKind(KW_MOVE)||
					 celem.getFirstToken().isKind(KW_XLOC)||
					 celem.getFirstToken().isKind(KW_YLOC))){
				binaryChain.type = INTEGER;
			} else if (chain.type.equals(FRAME) && 
					(first.isKind(KW_SHOW)||
					 first.isKind(KW_HIDE)||
					 first.isKind(KW_MOVE)) &&
					(celem.getFirstToken().isKind(KW_SHOW)||
					 celem.getFirstToken().isKind(KW_HIDE)||
					 celem.getFirstToken().isKind(KW_MOVE)||
					 celem.getFirstToken().isKind(KW_XLOC)||
					 celem.getFirstToken().isKind(KW_YLOC))){
				binaryChain.type = FRAME;
			} else if (chain.type.equals(IMAGE) &&
					(first.isKind(OP_WIDTH)||
					 first.isKind(OP_HEIGHT)) &&
					(celem.getFirstToken().isKind(OP_WIDTH) ||
					 celem.getFirstToken().isKind(OP_HEIGHT)||
					 celem.getFirstToken().isKind(KW_SCALE))){
						binaryChain.type = INTEGER;
			} else if (chain.type.equals(IMAGE) &&
					celem.type.equals(FRAME)){
						binaryChain.type = FRAME;
			} else if (chain.type.equals(IMAGE) &&
					celem.type.equals(FILE)){
						binaryChain.type = NONE;
			} else if (chain.type.equals(TypeName.IMAGE) &&
					(first.isKind(OP_GRAY)||
					 first.isKind(OP_BLUR)||
					 first.isKind(OP_CONVOLVE)) &&
					(celem.getFirstToken().isKind(OP_WIDTH)||
					 celem.getFirstToken().isKind(OP_HEIGHT)||
					 celem.getFirstToken().isKind(KW_SCALE))){
						binaryChain.type = IMAGE;
			} else if (chain.type.equals(TypeName.IMAGE) &&
					first.isKind(KW_SCALE) &&
					(celem.getFirstToken().isKind(OP_WIDTH)||
					 celem.getFirstToken().isKind(OP_HEIGHT)||
					 celem.getFirstToken().isKind(KW_SCALE))){
						binaryChain.type = IMAGE;
			} else if (chain.type.equals(TypeName.IMAGE) &&
					celem.getFirstToken().isKind(IDENT)){
						binaryChain.type = IMAGE;
			} else{
				throw new TypeCheckException("Type check Error");
			}
		}
		else if(t.isKind(BARARROW)){
			if (chain.type.equals(TypeName.IMAGE) &&
				(first.isKind(OP_GRAY)||
				first.isKind(OP_BLUR)||
				first.isKind(OP_CONVOLVE)) &&
				(celem.getFirstToken().isKind(OP_WIDTH)||
				celem.getFirstToken().isKind(OP_HEIGHT)||
				celem.getFirstToken().isKind(KW_SCALE))){
				binaryChain.type = IMAGE;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else {
			throw new TypeCheckException("Type check Error");
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0, e1;
		e0 = binaryExpression.getE0();
		e1 = binaryExpression.getE1();
		e0.visit(this, arg);
		e1.visit(this, arg);
		Token op = binaryExpression.getOp();
		
		if(op.kind.equals(PLUS) ||op.kind.equals(MINUS)){
			if (e0.type.equals(INTEGER) && e1.type.equals(INTEGER)){
				binaryExpression.type = INTEGER;
			} else if (e0.type.equals(IMAGE) && e1.type.equals(IMAGE)){
				binaryExpression.type = IMAGE;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else if(op.kind.equals(TIMES)){
			if (e0.type.equals(INTEGER) && e1.type.equals(INTEGER)){
				binaryExpression.type = INTEGER;
			} else if (e0.type.equals(INTEGER) && e1.type.equals(IMAGE)){
				binaryExpression.type = IMAGE;
			} else if (e0.type.equals(IMAGE) && e1.type.equals(INTEGER)){
				binaryExpression.type = IMAGE;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else if(op.kind.equals(DIV)){
			if (e0.type.equals(INTEGER) && e1.type.equals(INTEGER)){
				binaryExpression.type = INTEGER;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else if(op.kind.equals(LT) || op.kind.equals(GT) || op.kind.equals(LE) || op.kind.equals(GE) ){
			if (e0.type.equals(INTEGER) && e1.type.equals(INTEGER)){
				binaryExpression.type = BOOLEAN;
			} else if (e0.type.equals(BOOLEAN) && e1.type.equals(BOOLEAN)){
				binaryExpression.type = BOOLEAN;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else if(op.kind.equals(EQUAL) || op.kind.equals(NOTEQUAL)){
			if (e0.type.equals(e1.type)){
				binaryExpression.type = BOOLEAN;
			} else {
				throw new TypeCheckException("Type check Error");
			}
		} else {
			throw new TypeCheckException("Type check Error");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
	// TODO Auto-generated method stub
		symtab.enterScope();
		ArrayList<Statement> stats = block.getStatements();
		ArrayList<Dec> decs = block.getDecs();
		int i=0,j=0;
		while (j<stats.size() && i<decs.size()){	
			if (stats.get(j).firstToken.pos < decs.get(i).firstToken.pos){
				stats.get(j).visit(this, arg);
				j++;
			} else {
				decs.get(i).visit(this, arg);
				i++;
			}
		}
		while(i<decs.size()){
			decs.get(i).visit(this, arg);
			i++;
		}
		while(j<stats.size()){
			stats.get(j).visit(this, arg);
			j++;
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.type = BOOLEAN;
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if (filterOpChain.getArg().getExprList().size() != 0){
			throw new TypeCheckException("Type Check Error");
		}
		filterOpChain.type = IMAGE;
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple = frameOpChain.getArg();
		Token token = frameOpChain.firstToken;
		if (token.isKind(KW_SHOW)||
			token.isKind(KW_HIDE)){
			if (tuple.getExprList().size() != 0){
				throw new TypeCheckException("Type Check Error");
			}
			frameOpChain.type = NONE;
		}
		else if (token.isKind(KW_XLOC)||
				token.isKind(KW_YLOC)){
			if (tuple.getExprList().size() != 0){
				throw new TypeCheckException("Type Check Error");
			}
			frameOpChain.type = TypeName.INTEGER;
		}
		else if(token.isKind(KW_MOVE)){
			if (tuple.getExprList().size() != 2){
				throw new TypeCheckException("Type Check Error");
			}
			frameOpChain.type = NONE;
		}
		else{
			throw new TypeCheckException("Type Check Error			");
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first = identChain.getFirstToken();
		Dec d = symtab.lookup(first.getText());
		if (d == null){
			throw new TypeCheckException("Type Check Error");
		}
		identChain.type = d.getType();
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first = identExpression.getFirstToken();
		Dec d = symtab.lookup(first.getText());
		if (d == null){
			throw new TypeCheckException("Type Check Error");
		}
		identExpression.type = d.getType();
		identExpression.d = d;
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = ifStatement.getE();
		e.visit(this, arg);
		if(e.type != BOOLEAN){
			throw new TypeCheckException("Type Check Error");
		}
		Block block = ifStatement.getB();
		block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.type = INTEGER;
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = sleepStatement.getE();
		e.visit(this, arg);
		if(e.type!= INTEGER){
			throw new TypeCheckException("Type Check Error");
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = whileStatement.getE();
		e.visit(this, arg);
		if(e.type != BOOLEAN){
			throw new TypeCheckException("Type Check Error");
		}
		Block block = whileStatement.getB();
		block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(!symtab.insert(declaration.getIdent().getText(), declaration)){
			throw new TypeCheckException("TypeError");
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<ParamDec> array = program.getParams();
		for (int i=0; i<array.size(); i++){
			array.get(i).visit(this, arg);
		}
		Block block = program.getB();
		block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue i = assignStatement.getVar();
		i.visit(this, arg);
		Expression e = assignStatement.getE();
		e.visit(this, arg);
		if(!(e.type.equals(i.type))){
			throw new TypeCheckException("Type Check Error");
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first = identX.getFirstToken();
		Dec d = symtab.lookup(first.getText());
		if (d == null){
			throw new TypeCheckException("Type Check Error");
		}
		identX.dec = d;
		identX.type = d.getType();
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(!symtab.insert(paramDec.getIdent().getText(), paramDec)){
			throw new TypeCheckException("TypeError");
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.type = INTEGER;
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple = imageOpChain.getArg();
		Token token = imageOpChain.firstToken;
		imageOpChain.type = Type.getTypeName(token);
		if (token.isKind(OP_WIDTH)|| token.isKind( OP_HEIGHT)){
			if (tuple.getExprList().size() != 0){
				throw new TypeCheckException("Type Check Error");
			}
			imageOpChain.type = TypeName.INTEGER;
		}
		else if (token.isKind(KW_SCALE)){
			if (tuple.getExprList().size() != 1){
				throw new TypeCheckException("Type Check Error");
			}
			imageOpChain.type = TypeName.IMAGE;
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<Expression> elist = tuple.getExprList();
		for (int i=0;i<elist.size();i++){
			elist.get(i).visit(this, arg);
			if(elist.get(i).type.equals(INTEGER)){
				throw new TypeCheckException("Type Check Error");
			}	
		}
		
		return null;
	}


}
