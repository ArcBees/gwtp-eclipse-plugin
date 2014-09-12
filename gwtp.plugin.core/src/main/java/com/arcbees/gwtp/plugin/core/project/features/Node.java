/**
 * Copyright 2014 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.gwtp.plugin.core.project.features;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private T data;
    private final Node<T> parent;
    private List<Node<T>> children = new ArrayList<>();

    private Node(Node<T> parent, T data) {
        this.parent = parent;
        this.data = data;
    }

    public Node(T data) {
        this(null, data);
    }

    public Node<T> addChild(T data) {
        Node<T> child = new Node<>(this, data);
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
