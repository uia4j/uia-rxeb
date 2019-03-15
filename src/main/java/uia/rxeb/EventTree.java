package uia.rxeb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventTree {

    private List<EventNode> top;

    private List<EventNode> nodes;

    public EventTree() {
        this.top = new ArrayList<EventNode>();
        this.nodes = new ArrayList<EventNode>();
    }

    public EventNode createNode(String nodeName) {
        EventNode node = new EventNode(nodeName, null, this);
        this.top.add(node);
        return node;
    }

    public EventNode queryNode(String path) {
        Optional<EventNode> opt = this.nodes.stream()
                .filter(n -> path.equals(n.path))
                .findFirst();
        return opt.isPresent() ? opt.get() : null;
    }

    public List<EventNode> queryTags(String name) {
        return this.nodes.stream()
                .filter(n -> name.toUpperCase().equals(n.name))
                .collect(Collectors.toList());
    }

    public List<EventNode> queryDirty() {
        return this.nodes.stream()
                .filter(n -> n.getDirty() != 0)
                .collect(Collectors.toList());
    }

    public List<EventNode> queryPathTags(String path, boolean includeChildren) {
        ArrayList<EventNode> result = new ArrayList<EventNode>();

        EventNode node = queryNode(path);
        if (node == null) {
            return result;
        }
        if (node.isTag()) {
            result.add(node);
            return result;
        }

        if (includeChildren) {
            return this.nodes.stream()
                    .filter(n -> n.inBound(node.getLeft(), node.getRight()) && n.isTag())
                    .collect(Collectors.toList());
        }
        else {
            return node.getTags();
        }
    }

    public EventMediator createMediator() {
        int leftBound = 0;
        for (EventNode node : this.top) {
            leftBound = node.buildBound(leftBound);
        }
        return new EventMediator(this);
    }

    void println() {
        for (EventNode node : this.top) {
            node.println("");
        }
    }

    void saveNode(EventNode node) {
        this.nodes.add(node);
    }
}
