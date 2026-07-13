class ObjectField {
    public static void main(String[] args) {
        System.out.println(new Maker().makeAndUse());
    }
}

class Maker {
    public int makeAndUse() {
        Data d1;
        Data d2;
        int v1;
        int v2;
        d1 = new Data();
        d2 = new Data();
        v1 = d1.init(10);
        v2 = d2.init(20);
        return d1.get() + d2.get();
    }
}

class Data {
    int val;

    public int init(int x) {
        val = x;
        return val;
    }

    public int get() {
        return val;
    }
}
