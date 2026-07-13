class ExpressionOrder {
    public static void main(String[] args) {
        System.out.println(new Order().eval());
    }
}

class Order {
    public int eval() {
        int a;
        int b;
        int c;
        int r1;
        int r2;
        a = 2;
        b = 3;
        c = 4;
        r1 = a + b * c;
        r2 = (a + b) * c;
        return r1 + r2;
    }
}
