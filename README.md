# Hand written Compiler

Stage 1: 

Implement a scanner for the programming language with the following lexical structure.
comment ::= /* NOT(*/)* */
token ::= ident | keyword | frame_op_keyword | filter_op_keyword | image_op_keyword | boolean_literal
| int_literal | separator | operator
ident ::= ident_start ident_part* (but not reserved)
ident_start ::= A .. Z | a .. z | $ | _
ident_part ::= ident_start | ( 0 .. 9 )
int_literal ::= 0 | (1..9) (0..9)*
keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth
filter_op_keyword ∷= gray | convolve | blur | scale
image_op_keyword ∷= width | height
frame_op_keyword ∷= xloc | yloc | hide | show | move
boolean_literal ::= true | false
separator ::= ; | , | ( | ) | { | }
operator ::= | | & | == | != | < | > | <= | >= | + | - | * | / | % | ! | -> | |-> | <-


Stage 2:

Implement a recursive descent parser for the following context-free grammar:
program ::= IDENT block
program ::= IDENT param_dec ( ,​ ​param_dec )* block
paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN) IDENT
block ::= {​ ( dec | statement) * }
dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME) IDENT
statement ::= OP_SLEEP expression ;​ | whileStatement | ifStatement | chain ;​ | assign ;
assign ::= IDENT ASSIGN expression
chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
whileStatement ::= KW_WHILE (​ expression )​ block
ifStatement ::= KW_IF (​ expression )​ block
arrowOp ∷= ARROW | BARARROW
chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE
frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE
arg ::= ε | ( ​expression ( ,expression)* )
expression ∷= term ( relOp term)*
term ∷= elem ( weakOp elem)*
elem ∷= factor ( strongOp factor)*
factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE
| KW_SCREENWIDTH | KW_SCREENHEIGHT | (​ expression )
relOp ∷= LT | LE | GT | GE | EQUAL | NOTEQUAL
weakOp ∷= PLUS | MINUS | OR
strongOp ∷= TIMES | DIV | AND | MOD

Stage 3:

Modify your parser from Stage 2 to return an abstract syntax tree specified by the
following abstract syntax:
Program ∷= List<ParamDec> Block
ParamDec ∷= type ident
Block ∷= List<Dec> List<Statement>
Dec ∷= type ident
Statement ∷= SleepStatement | WhileStatement | IfStatement | Chain
| AssignmentStatement
SleepStatement ∷= Expression
AssignmentStatement ∷= IdentLValue Expression
Chain ∷= ChainElem | BinaryChain
ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
IdentChain ∷= ident
FilterOpChain ∷= filterOp Tuple
FrameOpChain ∷= frameOp Tuple
ImageOpChain ∷= imageOp Tuple
BinaryChain ∷= Chain (arrow | bararrow) ChainElem
WhileStatement ∷= Expression Block
IfStatement ∷= Expression Block
Expression ∷= IdentExpression | IntLitExpression | BooleanLitExpression
| ConstantExpression | BinaryExpression
IdentExpression ∷= ident
IdentLValue ∷= ident
IntLitExpression ∷= intLit
BooleanLitExpression ∷= booleanLiteral
ConstantExpression ∷= screenWidth | screenHeight
BinaryExpression ∷= Expression op Expression
Tuple :≔ List<Expression>
op ∷= relOp | weakOp | strongOp
type ∷= integer | image | frame | file | boolean | url

Stage 4:

In PDF

Stage 5:
In this assignment, we will implement code generation for part of the language.  The rest will be added in Assignment 6.  The abstract syntax shown below has been modified to indicate which part of the language we need to implement now and how they map into JVM  elements.
Program ∷= Name List<ParamDec> Block
        class Name implements Runnable{
             variables declared in List<ParamDec> are instance variables of the class
             public Name(String[] args){
                initialize instance variables with values from args.
                 }
             public static void main(String[] args){
                Name instance = new Name(args);
                        instance.run();
                 }
             
                 public void run(){
                declarations and statements from block
                 }
           }
ParamDec ∷= type ident
        instance variable in class, initialized with values from arg array
Block ∷= List<Dec>  List<Statement>
Decs are local variables in current scope of run method
Statements are executed in run method
Must label beginning and end of scope, and keep track of local variables, their slot in the local variable array, and their range of visibility.
Dec ∷= type ident
Assign a slot in the local variable array to this variable and save it in the new slot attribute in the  Dec class.
Statement ∷= SleepStatement | WhileStatement | IfStatement | Chain
             | AssignmentStatement
SleepStatement ∷= Expression
AssignmentStatement ∷= IdentLValue Expression
        store value of Expression into location indicated by IdentLValue
        
IMPORTANT:  
            insert the following statement into your code for an Assignment Statement
        after value of expression is put on top of stack and before it is written into the
            IdentLValue
            CodeGenUtils.genPrintTOS(GRADE, mv,assignStatement.getE().getType());
Chain ∷= ChainElem | BinaryChain
ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
IdentChain ∷= ident
FilterOpChain ∷= filterOp Tuple
FrameOpChain ∷= frameOp Tuple
ImageOpChain ∷= imageOp Tuple
BinaryChain ∷= Chain (arrow | bararrow)  ChainElem
WhileStatement ∷= Expression Block
              goto GUARD
   BODY     Block
   GUARD  Expression
                  IFNE  BODY
IfStatement ∷= Expression Block
                  Expression
                  IFEQ AFTER
              Block
       AFTER …
Expression ∷=   IdentExpression | IntLitExpression | BooleanLitExpression
         | ConstantExpression | BinaryExpression
always generate code to leave value of expression on top of stack.        
IdentExpression ∷= ident
       load value of variable (this could be a field or a local var)
IdentLValue ∷= ident
              store value on top of stack to this variable (which could be a field or local var)
IntLitExpression ∷= intLit
       load constant
BooleanLitExpression ∷= booleanLiteral
       load constant
ConstantExpression ∷= screenWidth | screenHeight
BinaryExpression ∷= Expression op Expression
      Visit children to generate code to leave values of arguments on stack
      perform operation, leaving result on top of the stack.  Expressions should
      be evaluated from left to write consistent with the structure of the AST.
Tuple :≔ List<Expression>
        op ∷= relOp | weakOp | strongOp
type ∷= integer | image | frame | file | boolean | url

Stage 6:

Program ∷= Name List<ParamDec> Block
        class Name implements Runnable{
             variables declared in List<ParamDec> are instance variables of the class
             public Name(String args){
                initialize instance variables with values from args.
                 }
             public static void main(String[] args){
                Name instance = new Name(args);
                        instance.run();
                 }
             
                 public void run(){
                declarations and statements from block
                 }
           }
ParamDec ∷= type ident
        instance variable in class, initialized with values from command line arguments
Block ∷= List<Dec>  List<Statement>
Decs are local variables in current scope of run method
Statements are executed in run method
Must label beginning and end of scope, and keep track of local variables, their slot in the local variable array, and their range of visibility.
If a statement was a BinaryChain, it will have left a value on top of the stack.  Check for this and pop it if necessary.
Dec ∷= type ident
Assign a slot in the local variable array to this variable and save it in the new slot attribute in the  Dec class.
frame maps to cop5556sp17.PLPRuntimeFrame
image maps to java.awt.image.BufferedImage
Statement ∷= SleepStatement | WhileStatement | IfStatement | Chain
             | AssignmentStatement
SleepStatement ∷= Expression
        invoke java/lang/Thread/sleep.  
Hint:  You will need to change the integer expression to a long  with “I2L”
        
AssignmentStatement ∷= IdentLValue Expression
store value of Expression into location indicated by IdentLValue
if the type of elements is image, this should copy the image.
use PLPRuntimeImageOps.copyImage
        
IMPORTANT:  
            insert the following statement into your code for an Assignment Statement
        after value of expression is put on top of stack and before it is written into the
            IdentLValue
            CodeGenUtils.genPrintTOS(GRADE, mv,assignStatement.getE().getType());
Chain ∷= ChainElem | BinaryChain        
ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
IdentChain ∷= ident
Handle the ident appropriately depending on its type and whether it is on the left or right side of binary chain.  
If on the left side, load its value or reference onto the stack.  
If this IdentChain is the right side of a binary expression,  
store the item on top of the stack into a variable (if INTEGER or IMAGE),
or write to file (if FILE),
or set as the image in the frame (if FRAME).
FilterOpChain ∷= filterOp Tuple
Assume that a reference to a BufferedImage is on top of the stack.
Generate code to nvoke the appropriate method from PLPRuntimeFilterOps.
FrameOpChain ∷= frameOp Tuple
Assume that a reference to a PLPRuntimeFrame is on top of the stack.
Visit the tuple elements to generate code to leave their values on top of the stack.
Generate code to invokethe appropriate method from PLPRuntimeFrame.
ImageOpChain ∷= imageOp Tuple
Assume that a reference to a BufferedImage  is on top of the stack.  
Visit the tuple elements to generate code to leave their values on top of the stack.
Generate code to invoke the appropriate method from PLPRuntimeImageOps or PLPRuntimeImageIO .
BinaryChain ∷= Chain (arrow | bararrow)  ChainElem
Visit the left expression.  
If the left Chain is a URL, generate code to invoke PLPRuntimeImageIO.readFromURL and leave a reference to a BufferedImage object on top of the stack.  
If the left expression is a File, generate code to invoke PLPRuntimeImageIO.readFromFile and leave a reference to a BufferedImage object on top of the stack.
Otherwise generate code to leave the left object on top of the stack.
Visit the right ChainElem and handle as given above.
Hint:  integers, for example, could appear on either side of a BinaryChain, in one the action is load, the other is store.  You need to figure out a way to communicate to the IdentChain which one.
Hint:  although some combinations have a type NONE, it is easiest to let all binary chain instances leave something on top of the stack.  In many cases, this value will be consumed by a parent.  At the top level it should be popped.  
 
WhileStatement ∷= Expression Block
              goto GUARD
   BODY     Block
   GUARD  Expression
                  IFNE  BODY
IfStatement ∷= Expression Block
                  Expression
                  IFEQ AFTER
              Block
       AFTER …
Expression ∷=   IdentExpression | IntLitExpression | BooleanLitExpression
         | ConstantExpression | BinaryExpression
always generate code to leave value of expression on top of stack.
        
IdentExpression ∷= ident
       load value of variable (this could be a field or a local var)
IdentLValue ∷= ident
              store value on top of stack to this variable (which could be a field or local var)
IntLitExpression ∷= intLit
       load constant
BooleanLitExpression ∷= booleanLiteral
       load constant
ConstantExpression ∷= screenWidth | screenHeight
Generate code to invoke PLPRuntimeFrame.getScreenWidth or PLPRuntimeFrame.getScreenHeight.
BinaryExpression ∷= Expression op Expression
Visit children to generate code to leave values of arguments on stack.
Generate code to perform operation, leaving result on top of the stack.
New in Assignment 6:  methods to add two images, subtract two images, etc.  Routines are provided in PLPRuntimeImageOps.
New in assignment 6:  implement &, |, and %.  
Expressions should be evaluated from left to write consistent with the structure of the AST.
You may need to modify your TypeCheckVisitor t
Tuple :≔ List<Expression>
Visit expressions to generate code to leave values on top of the stack
        op ∷= relOp | weakOp | strongOp  
implement operators and %
type ∷= integer | image | frame | file | boolean | url
