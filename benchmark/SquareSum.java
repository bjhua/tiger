class SquareSum {
    public static void main(String[] args) {
        System.out.println(new Calc().sumSquares(3));
    }
}

class Calc {
    public int sumSquares(int n) {
        int i;
        int sum;
        i = 1;
        sum = 0;
        while (i < n + 1) {
            sum = sum + this.square(i);
            i = i + 1;
        }
        return sum;
    }

    public int square(int x) {
        return x * x;
    }
}
