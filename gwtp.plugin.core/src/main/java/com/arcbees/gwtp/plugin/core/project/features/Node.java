package com.arcbees.gwtp.plugin.core.project.features;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private final T data;
    private final Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();

    private Node(final Node<T> parent, final T data) {
        this.parent = parent;
        this.data = data;
    }

    public Node(final T data) {
        this(null, data);
    }

    public Node<T> addChild(final T data) {
        final Node<T> child = new Node<>(this, data);
        children.add(child);
        return child;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public Node<T> getParent() {
        return parent;
    }

}
