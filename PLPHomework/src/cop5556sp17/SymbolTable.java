package cop5556sp17;


import cop5556sp17.AST.Dec;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
	
	
	//TODO  add fields
	int  current_scope, next_scope;
	
	public class MyValue {
		Dec dec;
		int i;
	  public MyValue(Dec dec,int i) {
	    this.dec = dec;
	    this.i = i;
	  }
	  public MyValue() {
	    this.dec = null;
	    this.i = 0;
	  }
	}
	HashMap<String,ArrayList<MyValue>> tab;
	ArrayList<Integer> scope_stack;
	
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		current_scope = next_scope++; 
		scope_stack.add(new Integer(current_scope));
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		int last = scope_stack.size()-1;
		scope_stack.remove(last);
		last = scope_stack.size()-1;
		current_scope = scope_stack.get(last);
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		ArrayList<MyValue> array = tab.get(ident);
		if (array == null){//if not found put into hash map
			array = new ArrayList<MyValue>();
			tab.put(ident, array);
		}
		for(int i=0;i<array.size();i++){
			if(array.get(i).i == current_scope){
				return false;
			}
		}
		array.add(new MyValue(dec,current_scope));//if found add into array
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		ArrayList<MyValue> array = tab.get(ident);
		if(array == null) return null;
		for(int i=0; i<array.size(); i++){
			for(int j=scope_stack.size() - 1; j >=0; j--){
				if (array.get(i).i == scope_stack.get(j)){
					return array.get(i).dec;
				}
			}
		}
		return null;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		tab =  new HashMap<String,ArrayList<MyValue>>();
		scope_stack = new ArrayList<Integer>();
		this.next_scope = 1;
		this.current_scope = 0;
		scope_stack.add(0);//adding default zero as global scope
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
}
