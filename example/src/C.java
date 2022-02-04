class C extends B{
    int m(A a, B b, C c) { 
        System.out.println("c");
        A[] aaa = new A[10];
        Object o1 = aaa;
        Object o2 = o1;
        A[] bbb = (A[]) o2;
        return 2; 
    } 

}