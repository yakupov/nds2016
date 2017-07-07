package ru.itmo.iyakupov.ss.treap2015;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ObjectiveComparator;
import ru.itmo.iyakupov.ss.IPopulation;

import java.util.*;

import static org.moeaframework.core.NondominatedSorting.CROWDING_ATTRIBUTE;

@SuppressWarnings("WeakerAccess")
public class Treap {
    private final Solution x;
    private final int y;

    private final Treap left;
    private final Treap right;

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
            final Treap newR = merge(l.right, r);
            final Treap res = new Treap(l.x, l.y, l.left, newR);
            res.verify();
            return res;
        } else {
            final Treap newL = merge(l, r.left);
            final Treap res = new Treap(r.x, r.y, newL, r.right);
            res.verify();
            return res;
        }
    }

    public List<Solution> toList() {
        final List<Solution> solutionList = new ArrayList<>();
        final Queue<Treap> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            final Treap t = queue.remove();
            solutionList.add(t.x);
            if (t.left != null)
                queue.add(t.left);
            if (t.right != null)
                queue.add(t.right);
        }

        assert (solutionList.size() == size());
        return solutionList;
    }

    public void verify() {
        //assert (toList().size() == size());
        //unneeded in prod
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

    private String toString(int level) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Treap{");
        sb.append("x=").append(Arrays.toString(x.getObjectives()));
        sb.append(", y=").append(y).append("\n");

        for (int i = 0; i < level; ++i) sb.append("\t");
        sb.append(", left=");
        sb.append(left == null ? null : left.toString(level + 1)).append("\n");

        for (int i = 0; i < level; ++i) sb.append("\t");
        sb.append(", right=");
        sb.append(right == null ? null : right.toString(level + 1)).append("\n");

        for (int i = 0; i < level; ++i) sb.append("\t");
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public int size() {
        return size;
    }

    public Solution getOrdered(int number) {
        if (number < 0 || number >= size)
            throw new IllegalArgumentException("Size = " + size + " but " + number + "-th element was requested");

        Treap workingTreap = this;
        int plusElements = number;
        while (plusElements > 0) {
            if (workingTreap.left != null) {
                if (plusElements > workingTreap.left.size) {
                    plusElements = plusElements - workingTreap.left.size - 1;
                    workingTreap = workingTreap.right;
                } else {
                    workingTreap = workingTreap.left;
                    --plusElements;
                }
            } else {
                --plusElements;
                workingTreap = workingTreap.right;
            }

            Objects.requireNonNull(workingTreap, "Strange situation, add debug output");
        }

        return workingTreap.x;
    }

    public Treap updateCrowdingDistance() {
        final List<Solution> nonDuplicates = new ArrayList<>(size());
        final Set<Solution> toEvict = new HashSet<>(size());

        final Queue<Treap> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            final Treap t = queue.remove();
            if (t.left != null)
                queue.add(t.left);
            if (t.right != null)
                queue.add(t.right);

            final Solution solution = t.x;
            solution.setAttribute(CROWDING_ATTRIBUTE, 0.0);
            for (Solution nd: nonDuplicates) {
                if (IPopulation.distance(nd, solution) < Settings.EPS) {
                    toEvict.add(solution);
                    break;
                }
            }

            if (!toEvict.contains(solution))
                nonDuplicates.add(solution);
        }

        Treap res = this;
        for (Solution solution : toEvict) {
            res = res.remove(solution);
        }

        // then compute the crowding distance for the unique solutions
        final int n = res.size();

        final List<Solution> front = res.toList();
        if (n < 3) {
            for (Solution solution : front)
                solution.setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);
        } else {
            final int numberOfObjectives = front.get(0).getNumberOfObjectives();
            for (int i = 0; i < numberOfObjectives; i++) {
                front.sort(new ObjectiveComparator(i));

                final double minObjective = front.get(0).getObjective(i);
                final double maxObjective = front.get(n - 1).getObjective(i);

                front.get(0).setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);
                front.get(n - 1).setAttribute(CROWDING_ATTRIBUTE, Double.POSITIVE_INFINITY);

                for (int j = 1; j < n - 1; j++) {
                    double distance = (Double)front.get(j).getAttribute(CROWDING_ATTRIBUTE);
                    distance += (front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i)) / (maxObjective - minObjective);
                    front.get(j).setAttribute(CROWDING_ATTRIBUTE, distance);
                }
            }
        }

        return res;
    }

    public Treap remove(Solution x) {
        final Treaps cut = splitX(x);

        if (cut.r.left == null) {
            cut.r = cut.r.right;
        } else {
            final List<Treap> stack = new LinkedList<>();
            Treap mutant = cut.r;
            while (mutant.left != null) {
                if (mutant.left.left == null) {
                    mutant = new Treap(mutant.x, mutant.y, mutant.left.right, mutant.right);
                    break;
                    // System.out.println("!!REMOVED!, now " + mutant.left);
                } else {
                    stack.add(mutant);
                    mutant = mutant.left;
                }
            }

            for (int i = stack.size() - 1; i >= 0; --i) {
                final Treap old = stack.get(i);
                mutant = new Treap(old.x, old.y, mutant, old.right);
            }

            cut.r = mutant;
        }
        if (cut.l != null) {
            cut.l.verify();
        }
        if (cut.r != null) {
            cut.r.verify();
        }

        final Treap res = Treap.merge(cut.l, cut.r);
        if (res != null)
            res.verify();
        return res;
    }
}
