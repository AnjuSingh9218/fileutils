package com.pe.fn;


public class Wrapper<X, Y> {
    private X x;
    private Y y;

    public Wrapper(X x, Y y){
        this.x = x;
        this.y = y;

    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }
}
