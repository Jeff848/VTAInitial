class Main {
    public static void main(String[] args) {
        A a1, a2, a3;
        B b1, b2, b3;
        C c;
        VeryUniqueName d;
        System.out.println("w");
        a1 = new A();
        a2 = new A();
        a3 = new A();
        b1 = new B();
        b2 = new B();
        b3 = new B();
        c = new C();
        d = new VeryUniqueName();
        a1 = a2;
        a3 = a1;
        a3 = b3;
        b3 = (B) a3;
        b1 = b2;
        int i = a1.m();
        int j = b3.m();
        int k = c.m(a1, b1, c);
        int l = d.testPhi(false);
        int m = a2.m();
        d.m(a1, b1, c);
        int n = a3.m();

    }
}