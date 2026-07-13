class Inherit {
    public static void main(String[] a) {
        System.out.println(new Doit().doit());
    }
}

class A {
    int i;
    int j;

    public int f() {
        return j;
    }
}

class B extends A {
    int i;
    int b_k;

    public int g() {
        return 1;
    }

    public int k() {
        return j;
    }

    public int f() {
        return i;
    }
}


class C extends A {
    int i;
    int c_k;

    public int k() {
        return 4;
    }

    public int g() {
        return 5;
    }

    public int f() {
        return 6;
    }


}

class Doit {
    public int doit() {
        return this.doit0(new B(), new C());
    }

    public int doit0(A x, A y) {
        int a;
        int b;

        a = x.f();
        b = y.f();
        return x.f() + new C().g();
    }
}