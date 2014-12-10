class QuickSort{
    public static void main(String[] a){
	System.out.println(new QS().Start(10));
    }
}

class QS{
    
    int[] number ;
    int size ;

    public int Start(int sz){
	int aux01 ;
	aux01 = this.Init(sz);
	aux01 = this.Print();
	System.out.println(9999);
	aux01 = size - 1 ;
	aux01 = this.Sort(0,aux01);
	aux01 = this.Print();
	return 0 ;
    }
}
