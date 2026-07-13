class InfiniteLoop {
    public static void main(String[] args) {
        System.out.println(new Loop().doit());
    }
}

class Loop {
    int x;
    int y;


    public int doit() {
        Loop v;
        int n;
        n = 0;

        while (n < 1000000) {
            v = new Loop();
            System.out.println(n);
            n = n + 1;
        }
        return 0;
    }
}
