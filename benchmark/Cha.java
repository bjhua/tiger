// Figure 1 in the paper:
// https://dl.acm.org/doi/epdf/10.1145/236337.236371

class Main {
    public static void main(String[] args) {
        System.out.println(new Cha().doit());
    }
}

class Cha {
    public int doit() {
        B b;
        A a;
        int result1;
        int result2;
        int result3;

        b = new B();
        result1 = b.bar(1);
        result2 = b.foo();
        a = b;
        result3 = a.foo();
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        return 0;
    }
}

class A {
    public int foo() {
        return 1;
    }
}

class B extends A {
    public int foo() {
        return 2;
    }

    // We have to rename this method,
    // because MiniJava does not support overloading.
    public int bar(int i) {
        return i + 100;
    }
}
