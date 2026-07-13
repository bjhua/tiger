// 完整测试：打印1到5的奇偶性
class TestEvenOdd {
    public static void main(String[] args) {
        System.out.println(new Printer().run());
    }
}

class Printer {
    public int run() {
        int i;
        int r;
        i = 1;
        while (i < 6) {
            r = this.printEvenOdd(i);
            i = i + 1;
        }
        return 0;
    }

//    public int printEvenOdd(int num) {
//        return 100;
//    }


    public int printEvenOdd(int num) {
        int temp;
        int isEven;
        temp = num;

        while (1 < temp) {
            temp = temp - 2;
        }

        if (temp < 1) {
            isEven = 1;
            System.out.println(1);
        } else {
            isEven = 0;
            System.out.println(0);
        }
        return isEven;
    }
}
