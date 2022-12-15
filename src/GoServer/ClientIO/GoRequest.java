package GoServer.ClientIO;

public class GoRequest {
    public int id;
    public String type;
    private final String content;
    private String response;
    private boolean requested;
    private boolean received;

    public boolean  isRequested()   { return requested; }
    public boolean  isReceived()    { return received;  }
    public void     setRequested()  { requested = true; }
    public void     setReceived()   { received  = true; }

    public GoRequest(int id, String type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;

        requested = false;
        received = false;
    }

    @Override
    public String toString() {
        return "#" + id + "|" + type + "|" + content;
    }
}
