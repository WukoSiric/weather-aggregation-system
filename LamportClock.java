public class LamportClock {
    private int timestamp = 0;
    
    public synchronized int getTime() {
        return timestamp;
    }
    
    public synchronized void tick() {
        timestamp++;
    }

    public synchronized void updateTimestamp(int receivedTimestamp) {
        timestamp = Math.max(timestamp, receivedTimestamp) + 1;
    }
}