package ru.itmo.nds.reference.treap2015;

public class Treap {
    public static class Treaps {
        Treap l, r;
        public Treaps() {};
        public Treaps(Treap l, Treap r) {
            super();
            this.l = l;
            this.r = r;
        }

        public String toString() {
            return "l=" + String.valueOf(l) + ", r=" + String.valueOf(r);
        }
    }

    public Double2DIndividual x;
    public int y;

    public Treap left;
    public Treap right;

    public Treap(Double2DIndividual x, int y, Treap left, Treap right) {
        this.x = x;
        this.y = y;
        this.left = left;
        this.right = right;
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

    public Treaps splitX(Double2DIndividual x) {
        Treaps res = new Treaps();
        Treaps t = new Treaps();
        if (this.x.compareX1(x) < 0) {
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
     * @param x
     * @return
     */
    public Treaps splitY(Double2DIndividual x) {
        Treaps res = new Treaps();
        Treaps t = new Treaps();
        if (this.x.compareX2(x) >= 0) {
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

    public boolean dominatesSomebody(Double2DIndividual nInd) {
        Treaps split = splitX(nInd);
        if (split.l != null && nInd.compareDom(split.l.getMax()) < 0)
            return true;
        if (split.r != null && nInd.compareDom(split.r.getMin()) < 0)
            return true;
        return false;
    }

    public boolean dominatedBySomebody(Double2DIndividual nInd) {
        Treaps split = splitX(nInd);
        //System.err.println("DBS: " + split);
        //System.err.println("_DBS: " + this);

        if (split.l != null && nInd.compareDom(split.l.getMax()) > 0)
            return true;
        if (split.r != null && nInd.compareDom(split.r.getMin()) > 0)
            return true;
        return false;
    }

    public Double2DIndividual getMinP() {
        Treap l = this;
        while (l.left != null)
            l = l.left;
        Treap r = this;
        while (r.right != null)
            r = r.right;
        return new Double2DIndividual(l.x.getX1(), r.x.getX2());
    }

    public Double2DIndividual getMin() {
        Treap l = this;
        while (l.left != null)
            l = l.left;
        return l.x;
    }

    public Double2DIndividual getMax() {
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
        sb.append("[" + x.toString() + "]; ");
        if (left != null)
            left.toString(sb);
        if (right != null)
            right.toString(sb);
    }
}
