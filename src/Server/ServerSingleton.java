package Server;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Server.Server.ServerSingleton class implements 'singleton' design pattern and is a 'global variable'
 * to  communicate between threads
 */
public class ServerSingleton {

    private static ServerSingleton communicator;
    private ArrayList<SrvThread> threads;
    private ArrayList<String> names;
    private final Lock lock = new ReentrantLock(true);

    /**
     * private constructor
     */
    private ServerSingleton(){
        threads = new ArrayList<SrvThread>();
        names = new ArrayList<String>();
    }

    /**
     * replaces public constructor - singleton design pattern
     * @return
     */
    public static ServerSingleton getSingleton() {
        if (communicator == null) {
            communicator = new ServerSingleton();
        }
        return communicator;
    }

    /**
     * adds new thread to the list
     * @param thread SrvThread with connected socket
     * @param name connected socket client's name
     */
    public void addNewThread(SrvThread thread, String name) {
        lock.lock();
        try {
            this.threads.add(thread);
            this.names.add(name);

        } catch (Exception e) {
            System.out.println("Cannot add user");
        }finally {
            lock.unlock();
        }
    }

    /**
     * searches for concrete thread
     * @param name name to be searched
     * @return thread corresponding to the name
     */
    public SrvThread getThreadByName(String name){
        int index = this.names.indexOf(name);
        return this.threads.get(index);
    }

    /**
     * searches and removes concrete thread and corresponding name from the list
     * @param name name to be searched
     */
    public void removeByName(String name){
        int index = this.names.indexOf(name);
        this.threads.remove(index);
        this.names.remove(index);
    }

    /**
     * @return list with all names
     */
    public ArrayList<String> getAllNames(){ return names; }
}