package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		slotNum = 1;
		//slotStack = new Stack<Integer>();
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;


	int slotNum;
	//Stack<Integer> slotStack;
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.

		// incrementer to assign slot values to all decs
		int incrementer = 0;

		for (ParamDec dec : program.getParams()){
			dec.slotnumber = incrementer++;
			cw.visitField(0, dec.getIdent().getText(), dec.getType().getJVMTypeDesc(), null, null);
			dec.visit(this, mv);
		}

		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		//TODO  visit the local variables

		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method




		cw.visitEnd();//end of class

		//generate class file and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		binaryChain.getE0().visit(this, 0);
		switch(binaryChain.getArrow().kind){
		case BARARROW: mv.visitInsn(DUP); break;
		default: switch(binaryChain.getE0().getType()){
					case URL: mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL",
							PLPRuntimeImageIO.readFromURLSig, false); break;
					case FILE: mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile",
							PLPRuntimeImageIO.readFromFileDesc, false); break;
					default: break;
		} break;
		}

		switch(binaryChain.getArrow().kind){
			case BARARROW: binaryChain.getE1().visit(this, 3);break;
			default: binaryChain.getE1().visit(this, 1); break;
		}
//		if (binaryChain.getArrow().isKind(Kind.BARARROW)) {
//			binaryChain.getE1().visit(this, 3);
//		} else {
//			binaryChain.getE1().visit(this, 1);
//		}
		if (binaryChain.getE1() instanceof IdentChain) {
			IdentChain identChain = (IdentChain) binaryChain.getE1();
			if ((identChain.declaration instanceof ParamDec)) {
				if (identChain.declaration.getType() == TypeName.INTEGER) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.declaration.getIdent().getText(),
							identChain.declaration.getType().getJVMTypeDesc());
				}
			} else {
				if (identChain.declaration.getType() == TypeName.INTEGER) {
					mv.visitVarInsn(ILOAD, identChain.declaration.slotnumber);
				} else {
					mv.visitVarInsn(ALOAD, identChain.declaration.slotnumber);
				}
			}
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		TypeName typeofE0 = binaryExpression.getE0().getType();
		TypeName typeofE1 = binaryExpression.getE1().getType();
		Token operator = binaryExpression.getOp();

		if(operator.kind == (PLUS) || operator.kind == MINUS || operator.kind==(DIV) || operator.kind==(TIMES)
				|| operator.kind==(MOD) || operator.kind==(LE) || operator.kind==(LT) || operator.kind==(GT)
				|| operator.kind==(GE) || operator.kind==(EQUAL) || operator.kind==(NOTEQUAL)){
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
		}
		switch (operator.kind) {

		case PLUS:
			if ((typeofE0==TypeName.INTEGER) && (typeofE1 == TypeName.INTEGER)) {
				mv.visitInsn(IADD);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}
			break;

		case MINUS:
			if ((typeofE0 == TypeName.INTEGER) && (typeofE1 == TypeName.INTEGER)) {
				mv.visitInsn(ISUB);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			}
			break;

		case TIMES:
			if ((typeofE0== TypeName.INTEGER) && (typeofE1== TypeName.INTEGER)) {
				mv.visitInsn(IMUL);
			} else if ((typeofE0== TypeName.INTEGER) && (typeofE1== TypeName.IMAGE)) {
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
			break;

		case DIV:
			if ((typeofE0== TypeName.INTEGER) && (typeofE1== TypeName.INTEGER)) {
				mv.visitInsn(IDIV);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			}
			break;

		case MOD:
			if ((typeofE0== TypeName.INTEGER) && (typeofE1== TypeName.INTEGER)) {
				mv.visitInsn(IREM);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			}
			break;

		case LT:
			Label ltslabel = new Label();
			mv.visitJumpInsn(IF_ICMPGE, ltslabel);
			mv.visitInsn(ICONST_1);
			Label ltelabel = new Label();
			mv.visitJumpInsn(GOTO, ltelabel);
			mv.visitLabel(ltslabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(ltelabel);
			break;

		case LE:
			Label leslabel = new Label();
			mv.visitJumpInsn(IF_ICMPGT, leslabel);
			mv.visitInsn(ICONST_1);
			Label leelabel = new Label();
			mv.visitJumpInsn(GOTO, leelabel);
			mv.visitLabel(leslabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(leelabel);
			break;

		case GT:
			Label gtslabel = new Label();
			mv.visitJumpInsn(IF_ICMPLE, gtslabel);
			mv.visitInsn(ICONST_1);
			Label gtelabel = new Label();
			mv.visitJumpInsn(GOTO, gtelabel);
			mv.visitLabel(gtslabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(gtelabel);
			break;

		case GE:
			Label geslabel = new Label();
			mv.visitJumpInsn(IF_ICMPLT, geslabel);
			mv.visitInsn(ICONST_1);
			Label geelabel = new Label();
			mv.visitJumpInsn(GOTO, geelabel);
			mv.visitLabel(geslabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(geelabel);
			break;

		case EQUAL: case NOTEQUAL:
				Label eslabel = new Label();
				if(operator.kind == EQUAL){
				if (typeofE0== TypeName.INTEGER || typeofE0== TypeName.BOOLEAN) {
					mv.visitJumpInsn(IF_ICMPNE, eslabel);
				} else {
					mv.visitJumpInsn(IF_ACMPNE, eslabel);
				}
				} else {
					if (typeofE0== TypeName.INTEGER || typeofE0== TypeName.BOOLEAN) {
						mv.visitJumpInsn(IF_ICMPEQ, eslabel);
					} else {
						mv.visitJumpInsn(IF_ACMPEQ, eslabel);
					}
				}
				mv.visitInsn(ICONST_1);
				Label eelabel = new Label();
				mv.visitJumpInsn(GOTO, eelabel);
				mv.visitLabel(eslabel);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(eelabel);
			break;

		case AND:
			binaryExpression.getE0().visit(this, arg);
			Label andslabel = new Label();
			mv.visitJumpInsn(IFEQ, andslabel);
			binaryExpression.getE1().visit(this, arg);
			mv.visitJumpInsn(IFEQ, andslabel);
			mv.visitInsn(ICONST_1);
			Label andelabel = new Label();
			mv.visitJumpInsn(GOTO, andelabel);
			mv.visitLabel(andslabel);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(andelabel);
			break;

		case OR:
			binaryExpression.getE0().visit(this, arg);
			Label orslabel = new Label();
			mv.visitJumpInsn(IFNE, orslabel);
			binaryExpression.getE1().visit(this, arg);
			mv.visitJumpInsn(IFNE, orslabel);
			mv.visitInsn(ICONST_0);
			Label orelabel = new Label();
			mv.visitJumpInsn(GOTO, orelabel);
			mv.visitLabel(orslabel);
			mv.visitInsn(ICONST_1);
			mv.visitLabel(orelabel);
			break;
			default:
				//do nothing
				// this is not reachable as input is already verified
				break;
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		Label bslabel = new Label();
		mv.visitLineNumber(block.getFirstToken().getLinePos().line, bslabel);
		mv.visitLabel(bslabel);

		//ArrayList<Statement> stats = block.getStatements();
		//ArrayList<Dec> decs = block.getDecs();

		for (Dec dec : block.getDecs()) {
			// dec.setSlotNum(slotNum++);
			dec.visit(this, mv);
		}
		// ArrayList<Statement> statements = block.getStatements();
		for (Statement statement : block.getStatements()) {
			statement.visit(this, mv);
			if (statement instanceof BinaryChain) {
				mv.visitInsn(POP);
			}
		}
		Label belabel = new Label();
		mv.visitLineNumber(0, belabel);
		mv.visitLabel(belabel);
		for (Dec dec : block.getDecs()) {
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getType().getJVMTypeDesc(), null, bslabel,belabel, dec.slotnumber);
			slotNum--;
			//slotStack.pop();
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {

		//TODO Implement this
		boolean v = booleanLitExpression.getValue();
		System.out.print("in bool lit");
		if(!v){
			mv.visitInsn(ICONST_0);
		} else {
			mv.visitInsn(ICONST_1);
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		//assert false : "not yet implemented";
		Token t = constantExpression.getFirstToken();
		switch(t.kind){
		case KW_SCREENHEIGHT:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight",
					PLPRuntimeFrame.getScreenHeightSig, false);
			break;
		case KW_SCREENWIDTH:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth",
					PLPRuntimeFrame.getScreenWidthSig, false);
			break;
		default: break;
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this

		//slotStack.push(slotNum++);
		//declaration.slotnumber = (slotStack.peek());
		declaration.slotnumber = (slotNum++);
		//System.out.println(declaration.slotnumber);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		switch (filterOpChain.getFirstToken().kind) {
		case OP_BLUR:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
			break;

		case OP_GRAY:
			if ((int) arg != 3) {
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
			break;

		case OP_CONVOLVE:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig,
					false);
			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		frameOpChain.getArg().visit(this, arg);
		switch (frameOpChain.getFirstToken().kind) {
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc,
					false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc,
					false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc,
					false);
			break;

		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc,
					false);
			break;

		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc,
					false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		Boolean side = (int) arg == 1;
		if (side) {
			if (identChain.declaration instanceof ParamDec) {
				switch (identChain.declaration.getType()) {
				case INTEGER:
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, identChain.declaration.getIdent().getText(),
							identChain.declaration.getType().getJVMTypeDesc());
					identChain.declaration.setInitialized(true);
					break;

				case FILE:
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.declaration.getIdent().getText(),
							identChain.declaration.getType().getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write",
							PLPRuntimeImageIO.writeImageDesc, false);
					identChain.declaration.setInitialized(true);
					break;

				default:
					break;
				}
			} else {
				switch (identChain.declaration.getType()) {
				case INTEGER:
					mv.visitVarInsn(ISTORE, identChain.declaration.slotnumber);
					identChain.declaration.setInitialized(true);
					break;

				case IMAGE:
					mv.visitVarInsn(ASTORE, identChain.declaration.slotnumber);
					identChain.declaration.setInitialized(true);

					break;

				case FILE:
					mv.visitVarInsn(ALOAD, identChain.declaration.slotnumber);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write",
							PLPRuntimeImageIO.writeImageDesc, false);
					identChain.declaration.setInitialized(true);
					break;

				case FRAME:
					if (identChain.declaration.getInitialized()) {
						mv.visitVarInsn(ALOAD, identChain.declaration.slotnumber);
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame",
								PLPRuntimeFrame.createOrSetFrameSig, false);
						mv.visitVarInsn(ASTORE, identChain.declaration.slotnumber);
					} else {
						mv.visitInsn(ACONST_NULL);
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame",
								PLPRuntimeFrame.createOrSetFrameSig, false);
						mv.visitVarInsn(ASTORE, identChain.declaration.slotnumber);
						identChain.declaration.setInitialized(true);
					}
					break;

				default:
					break;
				}
			}
		} else {
			if (identChain.declaration instanceof ParamDec) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.declaration.getIdent().getText(),
						identChain.declaration.getType().getJVMTypeDesc());
			} else {
				if (identChain.declaration.getType() == FRAME) {
					if (identChain.declaration.getInitialized()) {
						mv.visitVarInsn(ALOAD, identChain.declaration.slotnumber);
						} else {
						mv.visitInsn(ACONST_NULL);
					}
				} else {
					mv.visitVarInsn(ALOAD, identChain.declaration.slotnumber);
				}

			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		System.out.println("in ident exp");
		boolean v = identExpression.d instanceof ParamDec;
		if (v) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.d.getIdent().getText(),identExpression.d.getType().getJVMTypeDesc());
		} else {
			TypeName t = identExpression.getType();
			if (t== TypeName.INTEGER || t== TypeName.BOOLEAN) {
				mv.visitVarInsn(ILOAD, identExpression.d.slotnumber);
			} else {
				mv.visitVarInsn(ALOAD, identExpression.d.slotnumber);
			}
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		boolean v = identX.dec instanceof ParamDec;
		if (v) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, identX.dec.getIdent().getText(),identX.dec.getType().getJVMTypeDesc());
		} else {
			if (identX.dec.getType() == IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage",
						PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, identX.dec.slotnumber);
				identX.dec.setInitialized(true);
			} else if (identX.dec.getType() == TypeName.INTEGER || identX.dec.getType() == TypeName.BOOLEAN) {
				//System.out.println(identX.dec.slotnumber);
				mv.visitVarInsn(ISTORE, identX.dec.slotnumber);
				identX.dec.setInitialized(true);
			}else {
				mv.visitVarInsn(ASTORE, identX.dec.slotnumber);
				identX.dec.setInitialized(true);
			}
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label if_false = new Label();
		mv.visitJumpInsn(IFEQ, if_false);
		Label if_true = new Label();
		mv.visitLabel(if_true);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(if_false);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		imageOpChain.getArg().visit(this, arg);
		switch (imageOpChain.getFirstToken().kind) {
			case OP_WIDTH:
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", "()I", false);
				break;

			case OP_HEIGHT:
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", "()I", false);
				break;

			case KW_SCALE:
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
				break;

			default:
				break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.getValue());
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		MethodVisitor mv = (MethodVisitor) arg;
		TypeName typeName = paramDec.getType();
		switch (typeName) {
		case INTEGER:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(paramDec.slotnumber);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
			break;

		case BOOLEAN:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(paramDec.slotnumber);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
			break;

		case FILE:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(paramDec.slotnumber);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/io/File;");
			break;
		case URL:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(paramDec.slotnumber);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/net/URL;");
			break;
		default:
			break;
		}
		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//assert false : "not yet implemented";
		sleepStatement.getE().visit(this, arg);

		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//assert false : "not yet implemented";
		List<Expression> explist = tuple.getExprList();
		for (Expression exp : explist) {
			exp.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		Label wslabel = new Label();
		mv.visitJumpInsn(GOTO, wslabel);
		Label wblabel = new Label();
		mv.visitLabel(wblabel);
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(wslabel);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, wblabel);
		return null;
	}

}
