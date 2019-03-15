package uia.rxeb;

import java.util.ArrayList;
import java.util.List;

public class EventTag extends EventNode {

    private Object value;

    public EventTag(String name, EventNode parent, EventTree tree) {
        super(name.toUpperCase(), parent, tree);
        this.value = EventValue.UNKNOWN;
    }

    @Override
    public boolean isTag() {
        return true;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void updateValue(Object value) {
        EventNode node = this.parent;
        this.value = value;
        this.dirty = 1;
        node.dirty = 1;
        node = node.parent;
        while (node != null) {
            if (node.dirty == 0) {
                node.dirty = 2;
            }
            node = node.parent;
        }
    }

    @Override
    public List<EventNode> getTags() {
        ArrayList<EventNode> tags = new ArrayList<EventNode>();
        tags.add(this);
        return tags;
    }

    @Override
    public EventTag addTag(String tagName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventTag addNode(String nodeName) {
        throw new UnsupportedOperationException();
    }
}
