package ru.ifmo.steady.problem;

import ru.ifmo.steady.Problem;
import ru.ifmo.steady.Solution;

public class DTLZ1 implements Problem {
    private static final Problem instance = new DTLZ1();
    public static Problem instance() { return instance; }

    public double frontMinX() { return 0; }
    public double frontMaxX() { return 0.5; }
    public double frontMinY() { return 0; }
    public double frontMaxY() { return 0.5; }

    public int inputDimension() { return 6; }
    public String getName() { return "DTLZ1"; }

    public Solution evaluate(double[] input) {
        double gm = Common.gDTLZ1(input, 1);
        return new Solution(
            0.5 * input[0] * (1 + gm),
            0.5 * (1 - input[0]) * (1 + gm),
            input
        );
    }
}
