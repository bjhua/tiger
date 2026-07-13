class NestedIf {
    public static void main(String[] args) {
        System.out.println(new Nester().check(2, 4, 3));
    }
}

class Nester {
    public int check(int a, int b, int c) {
        int result;
        if (a < b)
            if (a < c)
                result = a;
            else
                result = c;
        else
            if (b < c)
                result = b;
            else
                result = c;
        return result;
    }
}