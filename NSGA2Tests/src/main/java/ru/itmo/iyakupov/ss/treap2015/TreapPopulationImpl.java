package ru.itmo.iyakupov.ss.treap2015;

import org.moeaframework.core.Solution;

import java.util.*;

public class TreapPopulationImpl {
    private final Set<double[]> individuals;
    private final List<Treap> ranks;
    private final Random random = new Random();

    public int size() {
        return individuals.size();
    }

    public TreapPopulationImpl() {
        this(new HashSet<>(), new ArrayList<>());
    }

    private TreapPopulationImpl(Set<double[]> individuals, List<Treap> ranks) {
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

        if (individuals.contains(nInd.getObjectives())) {
            return rank;
        } else {
            individuals.add(nInd.getObjectives());
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