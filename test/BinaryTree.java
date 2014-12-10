class BinaryTree{
    public static void main(String[] a){
	System.out.println(new BT().Start());
    }
}

class Tree{
    Tree left ;
    Tree right;
    int key ;
    boolean has_left ;
    boolean has_right ;
    Tree my_null ;


    // Insert a new element in the tree
    public boolean Insert(int v_key){
	Tree new_node ;
	boolean ntb ;
	boolean cont ;
	int key_aux ;

	
	new_node = new Tree();
	ntb = new_node.Init(v_key) ;
	current_node = this ;
	cont = true ;
	while (cont){
	    key_aux = current_node.GetKey();
	    if (v_key < key_aux){
		if (current_node.GetHas_Left())
		    current_node = current_node.GetLeft() ;
		else {
		    cont = false ;
		    ntb = current_node.SetHas_Left(true);
		    ntb = current_node.SetLeft(new_node);
		}
	    }
	    else{
		if (current_node.GetHas_Right())
		    current_node = current_node.GetRight() ;
		else {
		    cont = false ;
		    ntb = current_node.SetHas_Right(true);
		    ntb = current_node.SetRight(new_node);
		}
	    }
	}
	return true ;
    }
}
   
