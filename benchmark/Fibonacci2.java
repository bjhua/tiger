class Fibonacci2 {
    public static void main(String[] args) {
        System.out.println(new Fib().calc(6));
    }
}

class Fib {
    public int calc(int n) {
        int result;
        if (n < 2)
            result = n;
        else
            result = this.calc(n - 1) + this.calc(n - 2);
        return result;
    }
}
