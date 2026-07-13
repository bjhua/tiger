// 这是一个同时合法的MiniJava和Java程序
// 用于计算斐波那契数列的第n项
class Fibonacci {
    public static void main(String[] args) {
        // 计算第5个斐波那契数
        System.out.println(new FibCalc().compute(5));
    }
}

class FibCalc {
    public int compute(int num) {
        int result;
        int n1;
        int n2;
        int temp;
        int i;
        
        if (num < 1) {
            result = 0;
        } else if (num < 2) {
            result = 1;
        } else {
            n1 = 1;
            n2 = 1;
            i = 2;
            while (i < num) {
                temp = n1 + n2;
                n1 = n2;
                n2 = temp;
                i = i + 1;
            }
            result = n2;
        }
        return result;
    }
}
