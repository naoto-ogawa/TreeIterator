package com.example.tree01;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

public class Node<T> {

    private T t;

    public void setData(T t) {
        this.t = t;
    }

    public T getData() {
        return this.t;
    }

    private final List<Node<T>> children = new ArrayList<>();

    public Node<T> addChild(Node<T> child) {
        children.add(child);
        return this;
    }

    public StringBuilder info() {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString());
        sb.append("\n");
        for (Node<T> c : children) {
            sb.append(c.info());
        }
        return sb;
    }

    public ListIterator<Node<T>> listIterator1() {
        return new NodeIterator1(this);
    }

    public ListIterator<Node<T>> listIterator2() {
        return new NodeIterator2(this);
    }

    // -------------------------------------------------------------------------------------

    private class NodeIterator1 implements ListIterator<Node<T>> {

        private final Deque<List<Node<T>>> stackList = new ArrayDeque<>();

        private final Deque<Node<T>> stackNode = new ArrayDeque<>();

        private final Node<T> root;

        public NodeIterator1(Node<T> node) {
            root = node;
        }

        @Override
        public boolean hasNext() {

            if (stackNode.isEmpty()) {
                if (root.children.size() == 0) {
                    return false;
                }
                Node<T> c = root.children.get(0);
                stackNode.addFirst(c);
                stackList.add(root.children);
                return true;
            }

            // check children
            if (stackNode.peekFirst().children.size() == 0) {
                int pos = stackList.peekFirst().indexOf(stackNode.getFirst());
                if (pos + 1 < stackList.peekFirst().size()) {
                    // the same depth, so node change
                    stackNode.removeFirst();
                    stackNode.addFirst(stackList.getFirst().get(pos + 1));
                    return true;
                } else {
                    // go shallower AMAP
                    return goShallower();
                }

            } else {
                // go one level deeper
                stackList.addFirst(stackNode.getFirst().children);
                stackNode.addFirst(stackNode.getFirst().children.get(0));
                return true;
            }
        }

        private boolean goShallower() {
            stackNode.removeFirst();
            stackList.removeFirst();
            if (stackNode.size() == 0) {
                return false;
            }
            int pos1 = stackList.peekFirst().indexOf(stackNode.getFirst());
            if (pos1 + 1 < stackList.peekFirst().size()) {
                stackNode.removeFirst();
                stackNode.addFirst(stackList.getFirst().get(pos1 + 1));
                return true;
            } else {
                return goShallower();
            }
        }

        @Override
        public boolean hasPrevious() {
            if (stackNode.isEmpty()) {
                if (root.children.size() == 0) {
                    return false;
                }
                Node<T> c = root.children.get(root.children.size() - 1);
                stackNode.addFirst(c);
                stackList.add(root.children);

                goDeeper();
                return true;
            }

            // In hasPrevious method, stackNode.getFirst has already used.
            int pos1 = stackList.peekFirst().indexOf(stackNode.getFirst());
            if (pos1 <= 0) {
                // go shallower
                stackNode.removeFirst();
                stackList.removeFirst();
                return stackNode.size() != 0;
            }

            // same level, so get previous node and go deeper AMAP
            stackNode.removeFirst();
            stackNode.addFirst(stackList.getFirst().get(pos1 - 1));
            goDeeper();
            return true;
        }

        private void goDeeper() {
            Node<T> node = stackNode.getFirst();
            if (node.children.size() == 0) {
                return;
            }
            stackList.addFirst(node.children);
            stackNode.addFirst(node.children.get(node.children.size() - 1));
            goDeeper();
        }

        @Override
        public Node<T> next() {
            return stackNode.peekFirst();
        }

        @Override
        public Node<T> previous() {
            return stackNode.peekFirst();
        }

        @Override
        public void add(Node<T> tNode) {}

        @Override
        public int nextIndex() {return 0;}

        @Override
        public int previousIndex() {return 0;}

        @Override
        public void remove() {}

        @Override
        public void set(Node<T> tNode) {}
    }

    // -------------------------------------------------------------------------------------

    private class NodeIterator2 implements ListIterator<Node<T>> {

        private final Node<T> root;

        private final Deque<ListIterator<Node<T>>> stackIterator = new ArrayDeque<>();

        public NodeIterator2(Node<T> node) {
            root = node;
        }

        @Override
        public boolean hasNext() {

            if (stackIterator.isEmpty()) {
                if (root.children.size() == 0) {
                    return false;
                }
                stackIterator.addFirst(root.children.listIterator());
                return true;
            }

            while (stackIterator.size() > 0) {
                ListIterator<Node<T>> childIterator = stackIterator.removeFirst();
                if (childIterator.hasNext()) {
                    stackIterator.addFirst(childIterator);
                    return true;
                }
            }
            return false;
        }

        @Override
        public Node<T> next() {
            ListIterator<Node<T>> childIterator = stackIterator.peekFirst();
            Node<T> child = childIterator.next();
            if (child.children.size() > 0) {
                stackIterator.addFirst(child.children.listIterator());
            }
            return child;
        }

        @Override
        public boolean hasPrevious() {

            if (stackIterator.isEmpty()) {
                if (root.children.size() == 0) {
                    return false;
                }
                stackIterator.addFirst(root.children.listIterator(root.children.size()));
            }

            ListIterator<Node<T>> li = stackIterator.peekFirst();
            if (li.hasPrevious()) {
                Node<T> n = li.previous();
                li.next();
                while (n.children.size() > 0) {
                    li = n.children.listIterator(n.children.size());
                    stackIterator.addFirst(li);
                    n = li.previous();
                    li.next();
                }
                return li.hasPrevious();
            } else {
                stackIterator.removeFirst();
                return stackIterator.size() != 0 && stackIterator.peekFirst().hasPrevious();
            }
        }

        @Override
        public Node<T> previous() {
            ListIterator<Node<T>> childIterator = stackIterator.peekFirst();
            return childIterator.previous();
        }

        @Override
        public void add(Node<T> t) {}

        @Override
        public int nextIndex() {return 0;}

        @Override
        public int previousIndex() {return 0;}

        @Override
        public void remove() {}

        @Override
        public void set(Node<T> t) {}
    }


    // ------------------------------------------------------------------------------
    // Test
    // ------------------------------------------------------------------------------

    public static void main(String[] args) {
        Node<String> r = getNode("root");
        Node<String> l1a = getNode("[1] a");
        Node<String> l1b = getNode("[1] b");
        Node<String> l1c = getNode("[1] c");

        Node<String> l2aa = getNode("[2] a-a");
        Node<String> l2ab = getNode("[2] a-b");
//        Node<String> l2ac = getNode("[2] a-c");

        Node<String> l2ca = getNode("[2] c-a");
        Node<String> l2cb = getNode("[2] c-b");

        Node<String> l3aba = getNode("[3] a-b-a");
        Node<String> l3abb = getNode("[3] a-b-b");
        Node<String> l3abc = getNode("[3] a-b-c");

        r.addChild(l1a);
        r.addChild(l1b);
        r.addChild(l1c);
        l1a.addChild(l2aa);
        l1a.addChild(l2ab);
//        l1a.addChild(l2ac);
        l1c.addChild(l2ca);
        l1c.addChild(l2cb);
        l2ab.addChild(l3aba);
        l2ab.addChild(l3abb);
        l2ab.addChild(l3abc);

        System.out.println(r.info());

        System.out.println("------iterator #1 : forward and backward");
        ListIterator<Node<String>> iterator1f = r.listIterator1();

        iterateNext(iterator1f);
        System.out.println("-");
        iteratePrevious(iterator1f);

        System.out.println("------iterator #1 : backward and forward");
        ListIterator<Node<String>> iterator1b = r.listIterator1();

        iteratePrevious(iterator1b);
        System.out.println("-");
        iterateNext(iterator1b);

        System.out.println("------iterator #2 : forward and backward");
        ListIterator<Node<String>> iterator2f = r.listIterator2();

        iterateNext(iterator2f);
        System.out.println("-");
        iteratePrevious(iterator2f);

        System.out.println("------iterator #2 : backward and forward");
        ListIterator<Node<String>> iterator2b = r.listIterator2();

        iteratePrevious(iterator2b);
        System.out.println("-");
        iterateNext(iterator2b);

    }

    private static void iteratePrevious(ListIterator<Node<String>> iterator1f) {
        while (iterator1f.hasPrevious()) {
            Node<String> n = iterator1f.previous();
            System.out.println(n.getData());
        }
    }

    private static void iterateNext(ListIterator<Node<String>> iterator1f) {
        while (iterator1f.hasNext()) {
            Node<String> n = iterator1f.next();
            System.out.println(n.getData());
        }
    }

    private static Node<String> getNode(String data) {
        Node<String> n = new Node<>();
        n.setData(data);
        return n;
    }

}
