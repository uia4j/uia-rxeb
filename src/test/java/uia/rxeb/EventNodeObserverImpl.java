package uia.rxeb;

import java.util.Map;

public class EventNodeObserverImpl implements EventNodeObserver {

    public static long T = System.currentTimeMillis();

    private String id;

    public EventNodeObserverImpl(String id) {
        this.id = id;
    }

    @Override
    public void onUpdate(String path, Map<String, Object> data) {
        System.out.println(String.format("[%-2s] %6s %-10s: %s",
                Thread.currentThread().getId(),
                System.currentTimeMillis() - T,
                this.id, data));
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
