class MethodCall {
    public static void main(String[] args) {
        System.out.println(new Calc().run(2, 3));
    }
}

class Calc {
    public int run(int x, int y) {
        int sum;
        int prod;
        sum = this.add(x, y);
        prod = this.mul(x, y);
        return sum + prod;
    }
    public int add(int a, int b) {
        return a + b;
    }
    public int mul(int a, int b) {
        return a * b;
    }
}
