package com.zheng.linked;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Contended;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class LindList {
    private static Logger logger = LoggerFactory.getLogger(LindList.class);
    @Contended
    private volatile HandleContext head;
    @Contended
    private volatile HandleContext tail;
    @Contended
    private volatile long headId = 0;
//    @Contended
//    private volatile long id = 0;
    @Contended
    private volatile long tailId = 0;
    private AtomicBoolean headLock = new AtomicBoolean(false);
    private AtomicBoolean tailLock = new AtomicBoolean(false);
    private AtomicBoolean commonLock = new AtomicBoolean(false);
//    private final GameChannel gameChannel;

    private boolean registed = false;
    public boolean acquireLock(AtomicBoolean lock){
//        long timeMillis = System.currentTimeMillis();
//        int i = 0;
        while (true){
            boolean success = lock.compareAndSet(false, true);
            if (success){
                return true;
            }
//            i ++;
//            if (i > 30){
//                LockSupport.parkNanos(50);
//            }
//            if (System.currentTimeMillis() - timeMillis > 15000){
//                return false;
//            }
            LockSupport.parkNanos(50);
        }

    }
    public void releaseLock(boolean registed,AtomicBoolean lock){
        if (!registed){
            return;
        }
        lock.set(false);
    }

//    public GameChannel getGameChannel() {
//        return gameChannel;
//    }




    public LindList() {
//        this.gameChannel = ObjectUtil.checkNotNull(gameChannel,"gameChannel");
        head=new HandleContext(new HeadHandle());
        tail = new HandleContext(new TailHandler());
        tail.previous=head;
        head.next=tail;
    }

    private static class TailHandler implements Handler{

        @Override
        public void handle(HandleContext context, Object msg) {
            System.out.println("tail handler");
//            return context;
        }
    }

    /**
     * 头结点
     */
    private static class HeadHandle implements Handler{

        @Override
        public void handle(HandleContext context, Object msg) {
            System.out.println("head handle");
//            return context;
        }
    }

    public void addLast(HandleContext context){
        HandleContext temp = tail.previous;
        tail.previous=context;
        temp.next=context;
        context.next=tail;
        context.previous=temp;
        tailId++;
    }

    public HandleContext getFirst(){
        boolean b = acquireLock(headLock);
        try {
            long tempHead = headId;
            long tempTaiid = tailId;

            if (tempHead < tempTaiid){
                if (b){
                    return removeFirst();
                }
//
            }
            logger.info("head id bigger than tailid");
            return null;
        }finally {
            releaseLock(b,headLock);
        }


    }

    private HandleContext removeFirst() {
        if (headId >= tailId){
            return null;
        }
        HandleContext next = head.next;
        if (next == null){
            return null;
        }
        if (next == tail){
            return null;
        }

        head.next = next.next;
        next.next.previous = head;
        headId++;
        return next;
    }

    public void addLastContext( String name){
        boolean b = acquireLock(tailLock);
        try {
            if (b){
                HandleContext context = HandleContext.getInstance();

                context.setName(name);
                addLast(context);
            }

        }finally {
            releaseLock(b,tailLock);
        }




    }



}
