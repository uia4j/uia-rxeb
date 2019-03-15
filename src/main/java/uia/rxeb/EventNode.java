package uia.rxeb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventNode {

    public final String path;

    public final String name;

    public final EventNode parent;

    protected final EventTree tree;

    protected int dirty;

    private List<EventNode> children;

    private int left;

    private int right;

    public EventNode(String name, EventNode parent, EventTree tree) {
        this.parent = parent;
        this.tree = tree;
        this.children = new ArrayList<EventNode>();
        this.name = name;
        this.path = parent == null ? name : parent.path + "." + this.name;
        this.tree.saveNode(this);
    }

    public boolean isTag() {
        return false;
    }

    public Object getValue() {
        return null;
    }

    public void updateValue(Object value) {
    }

    public void flush() {
        this.dirty = 0;
    }

    public List<EventNode> getTags() {
        return this.children.stream()
                .filter(n -> n.isTag())
                .collect(Collectors.toList());
    }

    public EventNode addTag(String tagName) {
        EventNode node = new EventTag(tagName, this, this.tree);
        this.children.add(node);
        return node;
    }

    public EventNode addNode(String nodeName) {
        EventNode node = new EventNode(nodeName, this, this.tree);
        this.children.add(node);
        return node;
    }

    public int getDirty() {
        return this.dirty;
    }

    int getLeft() {
        return this.left;
    }

    void setLeft(int left) {
        this.left = left;
    }

    int getRight() {
        return this.right;
    }

    void setRight(int right) {
        this.right = right;
    }

    void println(String prefix) {
        System.out.println(String.format("%s%s(%s,%s)", prefix, this.path, this.left, this.right));
        for (EventNode node : this.children) {
            node.println(prefix + "  ");
        }
    }

    boolean inBound(int left, int right) {
        return this.left > left && this.right < right;
    }

    int buildBound(int leftBound) {
        this.left = leftBound + 1;
        this.right = this.left;
        for (EventNode node : this.children) {
            this.right = node.buildBound(this.right);
        }
        this.right++;
        return this.right;
    }

    @Override
    public String toString() {
        return this.path;
    }
}
