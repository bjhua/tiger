// 测试数组与循环
class ArraySum {
    public static void main(String[] args) {
        System.out.println(new ArrayOps().sum());
    }
}

class ArrayOps {
    public int sum() {
        int[] arr;
        int i;
        int total;
        int size;

        size = 5;
        arr = new int[size];
        
        // 给数组元素赋值
        arr[0] = 10;
        arr[1] = 20;
        arr[2] = 30;
        arr[3] = 40;
        arr[4] = 50;

        i = 0;
        total = 0;
        
        // 使用 .length 属性和 while 循环遍历
        while (i < arr.length) {
            total = total + arr[i];
            i = i + 1;
        }
        return total;
    }
}
