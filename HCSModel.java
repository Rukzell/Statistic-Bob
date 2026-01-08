package ac.anticheat.vertex.models;

import ac.anticheat.vertex.utils.MathUtil;

import java.util.List;

/**
 * @author Rukzell
 * Statistic Bob is a handcrafted statistical model for aim analysis (HCSModel)
 */
public class HCSModel {
    public static String modelName = "Statistic Bob";
    public static String modelVer = "1.1";

    public static double proba(List<Double> data) {
        double jb = MathUtil.jarqueBera(data);
        double entropy = MathUtil.entropy(data);
        double runs = MathUtil.runsTest(data);
        double z = MathUtil.madZ(data);
        double cusum = MathUtil.cusum(data);
        double peakRatio = MathUtil.peakEnergyRatio(data, 1);
        int extremeDeltas = 0;
        for (double d : data) {
            if (Math.abs(d) < 1E-4) {
                extremeDeltas++;
            }
        }

        double jbSignal;
        if (jb >= 20.0) {
            jbSignal = 0.0;
        } else if (jb <= 5.0) {
            jbSignal = 1.0;
        } else {
            jbSignal = (20.0 - jb) / 15.0;
        }

        double entropySignal;
        if (entropy >= 0.3) {
            entropySignal = 0.0;
        } else if (entropy <= 0.2) {
            entropySignal = 1.0;
        } else {
            entropySignal = (0.5 - entropy) / 0.3;
        }

        double zSignal;
        if (z >= 0.04) {
            zSignal = 0.0;
        } else {
            zSignal = (0.04 - z) / 0.04;
        }
        zSignal = Math.min(zSignal, 1.0);

        double cusumSignal;
        if (cusum >= 40.0) {
            cusumSignal = 0.0;
        } else if (cusum <= 32.0) {
            cusumSignal = 1.0;
        } else {
            cusumSignal = (50.0 - cusum) / (50.0 - 32.0);
        }

        double runsSignal;
        if (runs >= 0.45) {
            runsSignal = 0.0;
        } else if (runs <= 0.04) {
            runsSignal = 1.0;
        } else {
            runsSignal = (0.45 - runs) / (0.45 - 0.01);
        }

        double peakSignal;
        if (peakRatio <= 0.15) {
            peakSignal = 0.0;
        } else if (peakRatio >= 0.35) {
            peakSignal = 1.0;
        } else {
            peakSignal = (peakRatio - 0.15) / 0.20;
        }

        double extremeSignal;
        if (extremeDeltas <= 1) {
            extremeSignal = 0.0;
        } else if (extremeDeltas >= 3) {
            extremeSignal = 1.0;
        } else {
            extremeSignal = (extremeDeltas - 1.0) / 4.0;
        }


        double score =
                1.0 * jbSignal +
                        0.9 * entropySignal +
                        0.7 * zSignal +
                        1.1 * cusumSignal +
                        0.3 * runsSignal +
                        1.2 * peakSignal +
                        0.6 * extremeSignal;

        return 1.0 / (1.0 + Math.exp(-score));
    }
}
