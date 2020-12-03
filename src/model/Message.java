package model;

import java.io.Serializable;

public class Message implements Serializable {
    String action;
    Object object;

    public Message(String action, Object object) {
        this.action = action;
        this.object = object;
    }

    public Message(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
