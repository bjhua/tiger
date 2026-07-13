class ThisKeyword {
    public static void main(String[] args) {
        System.out.println(new ThisTest().compute(5));
    }
}

class ThisTest {
    public int compute(int x) {
        int y;
        y = this.square(x);
        return y;
    }
    public int square(int a) {
        return a * a;
    }
}