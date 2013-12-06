class ConstFold { 
	public static void main(String[] a) {
        System.out.println(new Doit().doit());
    }
}

class Doit {
    public int doit() {
    	int x;
    	
    	x = 999;
    	
        if ((x*0)<2)
          System.out.println(1);
        else 
          System.out.println(0);
        return 0;
    }
}
