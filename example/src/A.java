class E {

}

class A extends Object{
    int f;  // instance field
    static int h = 10;  // static field
    VeryUniqueName e;
    int m() {
        System.out.println("a"); 
        f = 2;
        e = new VeryUniqueName();
        VeryUniqueName d = this.e;
        d.m();
        return 0; 
    }
    static int n() { return 3; }    
}