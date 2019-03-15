package uia.rxeb;

import java.util.Map;

public interface EventNodeObserver {

    public void onUpdate(String path, Map<String, Object> data);
}
