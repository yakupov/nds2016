package ru.itmo.iyakupov.ss.treap2015;

import org.moeaframework.core.Solution;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Treap {
    public static class Treaps {
        public Treap l, r;

        public Treaps() {}

//        public Treaps(Treap l, Treap r) {
//            super();
//            this.l = l;
//            this.r = r;
//        }

        public String toString() {
            return "l=" + String.valueOf(l) + ", r=" + String.valueOf(r);
        }
    }

    public final Solution x;
    public final int y;

    public Treap left;
    public Treap right;

    private final int size;

    public Treap(Solution x, int y, Treap left, Treap right) {
        this.x = x;
        this.y = y;
        this.left = left;
        this.right = right;

        int size = 1;
        if (left != null) size += left.size;
        if (right != null) size += right.size;
        this.size = size;
    }

    public static Treap merge(Treap l, Treap r) {
        if (l == null) return r;
        if (r == null) return l;

        if (l.y > r.y) {
            Treap newR = merge(l.right, r);
            return new Treap(l.x, l.y, l.left, newR);
        } else {
            Treap newL = merge(l, r.left);
            return new Treap(r.x, r.y, newL, r.right);
        }
    }

    private static int compareX1(Solution t, Solution o) {
        return Double.compare(t.getObjective(0), o.getObjective(0));
    }

    private static int compareX2(Solution t, Solution o) {
        return Double.compare(t.getObjective(1), o.getObjective(1));
    }

    /**
     * not normalized
     */
    public static int compareDom(Solution t, Solution o) {
        return compareX1(t, o) + compareX2(t, o);
    }

    public Treaps splitX(Solution x) {
        Treaps res = new Treaps();
        Treaps t = new Treaps();
        if (compareX1(this.x, x) < 0) {
            if (right == null) {
                res.r = null;
            } else {
                t = right.splitX(x);
                res.r = t.r;
            }
            res.l = new Treap(this.x, y, left, t.l);
        } else {
            if (left == null) {
                res.l = null;
            } else {
                t = left.splitX(x);
                res.l = t.l;
            }
            res.r = new Treap(this.x, y, t.r, right);
        }
        //System.err.println("SPL: " + String.valueOf(this.x) + "_" + String.valueOf(left) + "_" + String.valueOf(right) + " : " + res.toString());
        return res;
    }

    /**
     * same as split X - members of r have greater x1 than members of l
     */
    public Treaps splitY(Solution x) {
        Treaps res = new Treaps();
        Treaps t = new Treaps();
        if (compareX2(this.x, x) >= 0) {
            if (right == null) {
                res.r = null;
            } else {
                t = right.splitY(x);
                res.r = t.r;
            }
            res.l = new Treap(this.x, y, left, t.l);
        } else {
            if (left == null) {
                res.l = null;
            } else {
                t = left.splitY(x);
                res.l = t.l;
            }
            res.r = new Treap(this.x, y, t.r, right);
        }
        return res;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean dominatesSomebody(Solution nInd) {
        final Treaps split = splitX(nInd);
        if (split.l != null && compareDom(nInd, split.l.getMax()) < 0)
            return true;
        else if (split.r != null && compareDom(nInd, split.r.getMin()) < 0)
            return true;
        else
            return false;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean dominatedBySomebody(Solution nInd) {
        final Treaps split = splitX(nInd);
        //System.err.println("DBS: " + split);
        //System.err.println("_DBS: " + this);

        if (split.l != null && compareDom(nInd, split.l.getMax()) > 0)
            return true;
        else if (split.r != null && compareDom(nInd, split.r.getMin()) > 0)
            return true;
        else
            return false;
    }

    public Solution getMinP() {
        Treap l = this;
        while (l.left != null)
            l = l.left;
        Treap r = this;
        while (r.right != null)
            r = r.right;
        return new Solution(new double[]{l.x.getObjective(0), r.x.getObjective(1)});
    }

    public Solution getMin() {
        Treap l = this;
        while (l.left != null)
            l = l.left;
        return l.x;
    }

    public Solution getMax() {
        Treap r = this;
        while (r.right != null)
            r = r.right;
        return r.x;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }

    protected void toString(StringBuilder sb) {
        sb.append("[").append(x.toString()).append("]; ");
        if (left != null)
            left.toString(sb);
        if (right != null)
            right.toString(sb);
    }

    public int size() {
        return size;
    }

    public Treap getOrdered(int number) {
        if (number < 0 || number >= size)
            throw new IllegalArgumentException("Size = " + size + " but " + number + "-th element was requested");

        Treap workingTreap = this;
        int plusElements = number;
        while (plusElements > 0) {
            if (workingTreap.left != null) {
                if (plusElements == workingTreap.left.size)
                    return workingTreap;
                else if (plusElements > workingTreap.left.size) {
                    plusElements = plusElements - workingTreap.left.size - 1;
                    workingTreap = workingTreap.right;
                } else
                    workingTreap = workingTreap.left;
            } else {
                --plusElements;
                workingTreap = workingTreap.right;
            }

            Objects.requireNonNull(workingTreap, "Strange situation, add debug output");
        }

        return workingTreap;
    }
}
