package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	public TypeName type;
	public Dec d;
	protected Expression(Token firstToken) {
		super(firstToken);
		this.type = null;
		this.d = null;
	}

	public TypeName getType(){
		return type;
	}
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
