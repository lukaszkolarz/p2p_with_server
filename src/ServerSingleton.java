import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerSingleton {
    private static ServerSingleton communicator;
    private ArrayList<SrvThread> threads;
    private ArrayList<String> names;
    private final Lock lock = new ReentrantLock(true);

    private ServerSingleton(){
        threads = new ArrayList<SrvThread>();
        names = new ArrayList<String>();
    }

    public static ServerSingleton getSingleton() {
        if (communicator == null) {
            communicator = new ServerSingleton();
        }
        return communicator;
    }

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

    public SrvThread getThreadByName(String name){
        int index = this.names.indexOf(name);
        return this.threads.get(index);
    }

    public void removeByName(String name){
        int index = this.names.indexOf(name);
        this.threads.remove(index);
        this.names.remove(index);
    }

    public ArrayList<String> getAllNames(){ return names; }


}