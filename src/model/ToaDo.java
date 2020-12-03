package model;

import java.io.Serializable;

public class ToaDo implements Serializable {
    private int x;
    private int y;

    public ToaDo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ToaDo() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
