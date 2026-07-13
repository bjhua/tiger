class BoolOps {
    public static void main(String[] args) {
        System.out.println(new BoolTester().test(5, 3));
    }
}

class BoolTester {
    public int test(int a, int b) {
        boolean flag;
        int result;
        flag = (a < b) && (a < b + 1);
        if (flag)
            result = 1;
        else
            result = 0;
        return result;
    }
}
