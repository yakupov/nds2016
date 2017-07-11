package ru.itmo.nds.reference.treap2015;

import java.util.*;

public class TreapPopulation {
    private final Set<Double2DIndividual> individuals;
    private final List<Treap> ranks;
    private final Random random = new Random();

    public int size() {
        return individuals.size();
    }

    public TreapPopulation() {
        this(new HashSet<>(), new ArrayList<>());
    }

    public TreapPopulation(Set<Double2DIndividual> individuals, List<Treap> ranks) {
        this.individuals = individuals;
        this.ranks = ranks;
    }

    @Deprecated
    protected int determineRankStupid(Double2DIndividual nInd) {
        int currRank = 0;
        for (int i = 0; i < ranks.size(); ++i) {
            if (ranks.get(i).dominatedBySomebody(nInd))
                currRank = i + 1;
            else
                return currRank;
        }
        return currRank;
    }

    protected int determineRank(Double2DIndividual nInd) {
        int currRank = 0;
        int l = 0;
        int r = ranks.size() - 1;
        while (l <= r) {
            int i = (l + r) / 2;
            if (ranks.get(i).dominatedBySomebody(nInd)) {
                currRank = i + 1;
                l = i + 1;
            } else {
                r = i - 1;
            }
        }
        return currRank;
    }

    public void addPoint(Double2DIndividual nInd) {
        if (individuals.contains(nInd)) {
            return;
        } else {
            individuals.add(nInd);
        }
        int rank = determineRank(nInd);
        //System.err.println(rank + "_" + nInd.toString() + "_" + ranks.size());
        Treap nTreap = new Treap(nInd, random.nextInt(), null, null);

        if (rank >= ranks.size()) {
            ranks.add(nTreap);
        } else if (nInd.compareDom(ranks.get(rank).getMinP()) < 0) {
            ranks.add(rank, nTreap);
            return;
        } else {
            int i = 0;
            Double2DIndividual minP = nInd;
            Treap cNext = nTreap;
            while (minP != null) {
                if (ranks.size() <= rank + i) {
                    ranks.add(cNext);
                    break;
                }

                boolean printTreaps = "Y".equals(System.getProperty("printTreaps"));
                printTreap(cNext, printTreaps);
                printTreap(ranks.get(rank + i), printTreaps);

                Treap.Treaps t1 = ranks.get(rank + i).splitX(minP);
                Treap.Treaps tr = new Treap.Treaps();
                if (t1.r != null)
                    tr = t1.r.splitY(minP);

                printTreap(t1.l, printTreaps);
                printTreap(t1.r, printTreaps);
                printTreap(tr.l, printTreaps);
                printTreap(tr.r, printTreaps);

                Treap res = Treap.merge(t1.l, cNext);
                printTreap(res, printTreaps);
                res = Treap.merge(res, tr.r);
                printTreap(res, printTreaps);
                ranks.set(rank + i, res);
                cNext = tr.l;
                printTreap(cNext, printTreaps);

                if (cNext == null)
                    break;
                minP = cNext.getMinP();
                i++;
            }
        }
    }

    private void printTreap(Treap cNext, boolean sw) {
        if (sw)
            System.err.println(cNext);
    }

    public static class IndWithRank {
        Double2DIndividual ind;
        int rank;
        public IndWithRank(Double2DIndividual ind, int rank) {
            super();
            this.ind = ind;
            this.rank = rank;
        }

        public String toString() {
            return "Rank = " + rank + ", value = " + String.valueOf(ind);
        }
    }

    public IndWithRank getRandWithRank() {
        if (individuals.size() == 0)
            throw new RuntimeException("Can't get random individual from empty population");
        Double2DIndividual randInd = (Double2DIndividual) individuals.toArray()[random.nextInt(individuals.size())];
        return new IndWithRank(randInd, detRankOfExPoint(randInd));
    }

    protected int detRankOfExPoint(Double2DIndividual ind) {
        int r = ranks.size() - 1;
        int l = 0;
        int result = -1;
        while (l != r) {
            int curr = (l + r)/2;
            if (r == l + 1) {
                curr = l;
                boolean dominated = ranks.get(curr).dominatedBySomebody(ind);
                boolean dominates = ranks.get(curr).dominatesSomebody(ind);
                if (!dominates && !dominated)
                    return result < 0 ? curr : Math.min(curr, result);

                curr = r;
                dominated = ranks.get(curr).dominatedBySomebody(ind);
                dominates = ranks.get(curr).dominatesSomebody(ind);
                if (!dominates && !dominated)
                    return result < 0 ? curr : Math.min(curr, result);
            } else {
                boolean dominated = ranks.get(curr).dominatedBySomebody(ind);
                boolean dominates = ranks.get(curr).dominatesSomebody(ind);
                if (!dominates && !dominated) {
                    result = result < 0 ? curr : Math.min(curr, result);
                    r = curr;
                } else if (dominates)
                    r = curr;
                else
                    l = curr;
            }
        }
        boolean dominated = ranks.get(l).dominatedBySomebody(ind);
        boolean dominates = ranks.get(l).dominatesSomebody(ind);
        if (!dominates && !dominated)
            result = result < 0 ? l : Math.min(l, result);

        if (result > 0)
            return result;
        throw new RuntimeException("Can't determine rank for " + ind.toString());
    }


    protected int detRankOfExPointDumb(Double2DIndividual ind) {
        for (int l = 0; l < ranks.size(); ++l) {
            boolean dominated = ranks.get(l).dominatedBySomebody(ind);
            boolean dominates = ranks.get(l).dominatesSomebody(ind);
            if (!dominates && !dominated)
                return l;
        }
        throw new RuntimeException("Can't determine rank for " + ind.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranks.size(); ++i) {
            sb.append(i);
            sb.append('\n');
            sb.append(ranks.get(i).toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    public void validate() {
        for (int i = 0; i < ranks.size(); ++i) {
            checkDom(ranks.get(i), i);
        }
    }

    private void checkDom(Treap t, int rk) {
        if (t == null)
            return;
        int cRankCalcd = 0;
        Double2DIndividual rankDeterminator = null;
        for (Double2DIndividual ind : individuals) {
            if (ind != t.x) {
                if (t.x.compareDom(ind) > 0) { //dominated
                    int newPossibleRank = detRankOfExPoint(ind) + 1;
                    if (newPossibleRank > cRankCalcd) {
                        cRankCalcd = newPossibleRank;
                        rankDeterminator = ind;
                    }
                }
            }
        }
        if (cRankCalcd != rk) {
            throw new RuntimeException("Population is sorted incorrectly. Point = " + t.x.toString() +
                    ", rk = " + rk + ", should be = " + cRankCalcd + ", determinator = " + rankDeterminator);
        }
        checkDom(t.left, rk);
        checkDom(t.right, rk);
    }
}