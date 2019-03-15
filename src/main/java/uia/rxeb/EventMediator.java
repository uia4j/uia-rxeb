package uia.rxeb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.reactivex.Observable;

public class EventMediator {

    private final EventTree tree;

    private final TreeMap<String, List<EventNodeObserver>> registryS;

    private final TreeMap<String, List<EventNodeObserver>> registryC;

    EventMediator(EventTree tree) {
        this.tree = tree;
        this.registryS = new TreeMap<String, List<EventNodeObserver>>();
        this.registryC = new TreeMap<String, List<EventNodeObserver>>();
    }

    public synchronized void register(final String path, final EventNodeObserver observer) {
        register(path, observer, false);
    }

    public synchronized void register(final String path, final EventNodeObserver observer, boolean includeChildren) {
        EventNode node = this.tree.queryNode(path);
        if (node == null) {
            throw new IllegalArgumentException(path + " not found");
        }

        if (includeChildren) {
            List<EventNodeObserver> observers = this.registryC.get(path);
            if (observers == null) {
                observers = new ArrayList<EventNodeObserver>();
                this.registryC.put(path, observers);
            }
            observers.add(observer);
        }
        else {
            List<EventNodeObserver> observers = this.registryS.get(path);
            if (observers == null) {
                observers = new ArrayList<EventNodeObserver>();
                this.registryS.put(path, observers);
            }
            observers.add(observer);
        }

        Observable.<Map<String, Object>> create(e -> {
            e.onNext(toValues(this.tree.queryPathTags(node.path, includeChildren)));
            e.onComplete();
        }).subscribe(d -> {
            observer.onUpdate(path, d);
        }, Throwable::printStackTrace);
    }

    public synchronized void emit(String tagPath, Object value) {
        EventNode node = this.tree.queryNode(tagPath);
        if (!node.isTag()) {
            throw new IllegalArgumentException(tagPath + " is not a tag");
        }
        node.updateValue(value);

        flushDirty();
    }

    public synchronized void emit(final Map<String, Object> tagValues) {
        for (Map.Entry<String, Object> e : tagValues.entrySet()) {
            EventNode node = this.tree.queryNode(e.getKey());
            if (!node.isTag()) {
                continue;
            }
            node.updateValue(e.getValue());
        }

        flushDirty();
    }

    public synchronized void flush(String path) {
        EventNode node = this.tree.queryNode(path);
        if (node == null) {
            return;
        }

        flushSelf(node);
        flushDetail(node);
        node.flush();
    }

    private void flushDirty() {
        this.tree.queryDirty().forEach(d -> {
            flushSelf(d);
            flushDetail(d);
            d.flush();
        });
    }

    private void flushSelf(final EventNode node) {
        if (node.getDirty() > 1) {
            return;
        }

        final List<EventNodeObserver> observersS = this.registryS.get(node.path);
        if (observersS != null && observersS.size() > 0) {
            final Map<String, Object> dataS = toValues(this.tree.queryPathTags(node.path, false));
            Observable.<Map<String, Object>> create(e -> {
                e.onNext(dataS);
                e.onComplete();
            }).subscribe(d -> {
                observersS.parallelStream().forEach(o -> o.onUpdate(node.path, dataS));
            }, Throwable::printStackTrace);
        }
    }

    private void flushDetail(final EventNode node) {
        final List<EventNodeObserver> observersC = this.registryC.get(node.path);
        if (observersC != null && observersC.size() > 0) {

            final Map<String, Object> dataC = toValues(this.tree.queryPathTags(node.path, true));
            Observable.<Map<String, Object>> create(e -> {
                e.onNext(dataC);
                e.onComplete();
            }).subscribe(d -> {
                observersC.parallelStream().forEach(o -> o.onUpdate(node.path, dataC));
            }, Throwable::printStackTrace);
        }
    }

    private Map<String, Object> toValues(List<EventNode> tags) {
        return tags.stream()
                .filter(n -> n.isTag())
                .collect(Collectors.toMap(n -> n.path, n -> n.getValue()));
    }
}
