class VeryUniqueName extends C {
    int m(A a, B b, C c) { 
        System.out.println("vun");
        return 3; 
    } 
    int testPhi(boolean bool) {
        A a;
        if(bool) {
            a = new B();
        } else {
            a = new C();
        }
        return a.m();
    }
}