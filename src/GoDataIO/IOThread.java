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

    @Override
    public void run() {
        if (ioStream == null) return;
        if (!finished) return;

        finished = false;
        ioAction();
//        System.out.println("[DEBUG] Finished Input File");
    }

    public abstract void ioAction();
}
