package ru.itmo.iyakupov.ss.treap2015;

import org.moeaframework.core.Solution;

import java.util.*;

public class TreapPopulationImpl {
    private final Set<Solution> individuals;
    private final List<Treap> ranks;
    private final Random random = new Random();

    public int size() {
        return individuals.size();
    }

    public TreapPopulationImpl() {
        this(new HashSet<>(), new ArrayList<>());
    }

    private TreapPopulationImpl(Set<Solution> individuals, List<Treap> ranks) {
        this.individuals = individuals;
        this.ranks = ranks;
    }

    public List<Treap> getRanks() {
        return ranks;
    }

    @Deprecated
    protected int determineRankStupid(Solution nInd) {
        int currRank = 0;
        for (int i = 0; i < ranks.size(); ++i) {
            if (ranks.get(i).dominatedBySomebody(nInd))
                currRank = i + 1;
            else
                return currRank;
        }
        return currRank;
    }

    private int determineRank(Solution nInd) {
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

    public int addPoint(Solution nInd) {
        int rank = determineRank(nInd);

        if (individuals.contains(nInd)) {
            return rank;
        } else {
            individuals.add(nInd);
        }

        //System.err.println(rank + "_" + nInd.toString() + "_" + ranks.size());
        final Treap nTreap = new Treap(nInd, random.nextInt(), null, null);

        if (rank >= ranks.size()) {
            ranks.add(nTreap);
        } else if (Treap.compareDom(nInd, ranks.get(rank).getMinP()) < 0) {
            ranks.add(rank, nTreap);
            //noinspection UnnecessaryReturnStatement
            return rank;
        } else {
            int i = 0;
            Solution minP = nInd;
            Treap cNext = nTreap;
            while (minP != null) {
                if (ranks.size() <= rank + i) {
                    ranks.add(cNext);
                    break;
                }

                boolean printTreaps = "Y".equals(System.getProperty("printTreaps"));
                printTreap(cNext, printTreaps);
                printTreap(ranks.get(rank + i), printTreaps);

                final Treaps t1 = ranks.get(rank + i).splitX(minP);
                Treaps tr = new Treaps();
                if (t1.r != null)
                    tr = t1.r.splitY(minP);

                printTreap(t1.l, printTreaps);
                printTreap(t1.r, printTreaps);
                printTreap(tr.l, printTreaps);
                printTreap(tr.r, printTreaps);

                Treap res = Treap.merge(t1.l, cNext);
                res.verify();
                printTreap(res, printTreaps);
                res = Treap.merge(res, tr.r);
                res.verify();
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
        return rank;
    }

    private void printTreap(Treap cNext, boolean sw) {
        if (sw)
            System.err.println(cNext);
    }

    private int detRankOfExPoint(Solution ind) {
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

//    public void validate() {
//        for (int i = 0; i < ranks.size(); ++i) {
//            checkDom(ranks.get(i), i);
//        }
//    }
}