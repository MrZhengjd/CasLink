package com.zheng.linked;



import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public  class HandleContext implements Cloneable, Serializable {

    private static AtomicInteger BaseId = new AtomicInteger(0);

    private  Handler handler;
    public HandleContext next;
    public HandleContext previous;


    private Object message;
    private String name;



    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }


//
    public void setName(String name) {
        this.name = name;
    }

    public HandleContext getPrevious() {
        return previous;
    }

    public void setPrevious(HandleContext previous) {
        this.previous = previous;
    }

    public HandleContext getNext() {
        return next;
    }

    public void setNext(HandleContext next) {
        this.next = next;
    }

    private HandleContext() {
    }

    public static HandleContext getInstance(){
        return Holder.context;
//
    }
    private static class Holder{
        private static HandleContext context = new HandleContext();
    }
    public HandleContext(String name) {
        this.name = name;
    }

    private String generateName(){
        return  "context-"+BaseId.getAndIncrement();
    }
    public HandleContext(Handler handler) {
        this.handler = handler;

//        this.executor = eventExecutor;
        this.name = generateName();

    }


    public void handleRequest(Object messge){

        handler.handle(this,messge);

//        return this;
    }



}
