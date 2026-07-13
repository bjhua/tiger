// 测试布尔逻辑与条件判断（无除法）
class EvenCheck {
    public static void main(String[] args) {
        // 测试数字 7
        System.out.println(new Checker().isEven(7));
    }
}

class Checker {
    public int isEven(int num) {
        int temp;
        int result;
        temp = num;
        
        // 通过反复减2来判断奇偶性
        while (1 < temp) {
            temp = temp - 2;
        }
        
        if (temp < 1) {
            result = 1;  // 偶数
        } else {
            result = 0;  // 奇数
        }
        return result;
    }
}
