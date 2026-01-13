package ac.anticheat.vertex.models;

import ac.anticheat.vertex.utils.MathUtil;

import java.util.List;

/**
 * @author Rukzell
 * Statistic Bob is a handcrafted statistical model for aim analysis (HCSModel)
 */
public class HCSModel {
    public static String modelName = "Statistic Bob";
    public static String modelVer = "1.3";

    public static double jbSignal;
    public static double zSignal;
    public static double extremeSignal;
    public static double autocorrelationSignal;
    public static double flatnessSignal;

    public static double proba(List<Double> data) {
        double jb = MathUtil.jarqueBera(data);
        double z = MathUtil.madZ(data);
        double ac1 = Math.abs(MathUtil.autocorrelation(data, 1));
        double ac2 = Math.abs(MathUtil.autocorrelation(data, 2));
        double autocorrelation = Math.max(ac1, ac2);
        double flatness = MathUtil.spectralFlatness(data);

        int extremeDeltas = 0;
        for (double d : data) {
            if (Math.abs(d) < 1E-4) {
                extremeDeltas++;
            }
        }

        jbSignal = signal(jb, 20.0, 5.0, true);
        zSignal = signal(z, 0.04, 0.0, true);
        extremeSignal = signal(extremeDeltas, 1.0, 2.0, false);
        autocorrelationSignal = signal(autocorrelation, 0.50, 0.20, true);
        flatnessSignal = signal(flatness, 0.30, 0.42, false);

        double score =
                1.2 * jbSignal +
                        0.9 * zSignal +
                        0.8 * extremeSignal +
                        1.8 * autocorrelationSignal +
                        1.4 * flatnessSignal;

        return Math.min(1.0, Math.max(0.0, score / 6.1));
    }

    public static double signal(double value, double legitMax, double cheatMin, boolean invert) {
        double v = invert ? -value : value;
        double legit = invert ? -legitMax : legitMax;
        double cheat = invert ? -cheatMin : cheatMin;

        if (v <= legit) return 0.0;
        if (v >= cheat) return 1.0;

        return (v - legit) / (cheat - legit);
    }

    public record HCSResult(double probability, double jb, double z, double extreme, double autocorrelation, double flatness) {}

    public static HCSResult evaluate(List<Double> data) {
        double proba = proba(data);
        return new HCSResult(
                proba,
                jbSignal,
                zSignal,
                extremeSignal,
                autocorrelationSignal,
                flatnessSignal
        );
    }
}
