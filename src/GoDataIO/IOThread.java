package GoDataIO;

public abstract class IOThread extends Thread {
    Object[] ioStream = new Object[1000];

    boolean finished = true;

    public boolean available() {
        return finished;
    }

    public IOThread() {}

    public IOThread(String name) {
        super(name);
    }

    public IOThread(String name, Object[] ioStream) {
        super(name);
        this.ioStream = ioStream;
    }

    public void reStartRead() {
        try {
            synchronized (this) {
                this.notify();
                additionAction();
                this.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] Cannot restart thread to start read saves.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (ioStream == null) return;
        if (!finished) return;

        finished = false;
        try {
            synchronized(this) {
                ioAction();
                this.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("[ERROR] Thread " + this.getName() + " interrupted.");
        }
//        System.out.println("[DEBUG] Finished Input File");
    }

    public void clean() {
        this.interrupt();
    }

    public abstract void ioAction();
    public abstract void additionAction();
}
