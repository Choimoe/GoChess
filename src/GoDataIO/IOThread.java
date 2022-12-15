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
        additionAction();
    }

    @Override
    public void run() {
        if (ioStream == null) return;
        if (!finished) return;

        finished = false;
        ioAction();
    }

    public void clean() {
        this.interrupt();
    }

    public abstract void ioAction();
    public abstract void additionAction();
}
