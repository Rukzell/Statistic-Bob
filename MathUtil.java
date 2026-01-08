package ac.anticheat.vertex.utils;

import java.util.*;

public class MathUtil {
    public static double jarqueBera(List<Double> data) {
        int n = data.size();
        if (n < 3) {
            return 0;
        }

        double mean = 0.0;
        for (double x : data) {
            mean += x;
        }
        mean /= n;

        double m2 = 0.0;
        double m3 = 0.0;
        double m4 = 0.0;

        for (double x : data) {
            double d = x - mean;
            double d2 = d * d;
            m2 += d2;
            m3 += d2 * d;
            m4 += d2 * d2;
        }

        m2 /= n;
        m3 /= n;
        m4 /= n;

        double skewness = m3 / Math.pow(m2, 1.5);
        double kurtosis = m4 / (m2 * m2);

        return (n / 6.0) *
                (skewness * skewness +
                        Math.pow(kurtosis - 3.0, 2) / 4.0);
    }

    public static double runsZScore(List<Double> values) {
        if (values == null || values.size() < 10) {
            return 0.0;
        }

        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double median = sorted.get(sorted.size() / 2);

        List<Integer> signs = new ArrayList<>();
        for (double v : values) {
            if (v > median) signs.add(1);
            else if (v < median) signs.add(-1);
        }

        if (signs.size() < 10) return 0.0;

        int runs = 1;
        for (int i = 1; i < signs.size(); i++) {
            if (!signs.get(i).equals(signs.get(i - 1))) {
                runs++;
            }
        }

        int n1 = 0, n2 = 0;
        for (int s : signs) {
            if (s == 1) n1++;
            else n2++;
        }

        double expectedRuns = (2.0 * n1 * n2) / (n1 + n2) + 1;
        double varianceRuns =
                (2.0 * n1 * n2 * (2.0 * n1 * n2 - n1 - n2)) /
                        (Math.pow(n1 + n2, 2) * (n1 + n2 - 1));

        if (varianceRuns <= 0) return 0.0;

        return (runs - expectedRuns) / Math.sqrt(varianceRuns);
    }

    public static double entropy(List<Double> data) {
        int bins = 10;
        double min = Collections.min(data);
        double max = Collections.max(data);
        double width = (max - min) / bins;

        if (width == 0) return 0;

        int[] hist = new int[bins];
        for (double v : data) {
            int b = Math.min(bins - 1, (int) ((v - min) / width));
            hist[b]++;
        }

        double h = 0;
        for (int c : hist) {
            if (c == 0) continue;
            double p = c / (double) data.size();
            h -= p * Math.log(p);
        }

        return h / Math.log(bins);
    }

    public static double runsTest(List<Double> data) {
        double median = data.stream().sorted().skip(data.size()/2).findFirst().orElse((double) 0);

        int runs = 1;
        boolean above = data.get(0) > median;

        for (double v : data) {
            boolean now = v > median;
            if (now != above) {
                runs++;
                above = now;
            }
        }

        return Math.abs(runs - data.size() / 2.0) / data.size();
    }

    public static double madZ(List<Double> data) {
        List<Double> sorted = new ArrayList<>(data);
        Collections.sort(sorted);

        double median = sorted.get(sorted.size() / 2);

        List<Double> dev = new ArrayList<>();
        for (double v : data) {
            dev.add(Math.abs(v - median));
        }

        Collections.sort(dev);
        double mad = dev.get(dev.size() / 2);
        if (mad == 0) return 0;

        return Math.abs(data.get(data.size() - 1) - median) / (1.4826 * mad);
    }

    public static double cusum(List<Double> data) {
        double mean = data.stream().mapToDouble(d -> d).average().orElse(0);
        double s = 0;
        double max = 0;

        for (double v : data) {
            s = Math.max(0, s + v - mean);
            max = Math.max(max, s);
        }
        return max;
    }

    public static double peakEnergyRatio(List<Double> data, int k) {
        List<Double> copy = new ArrayList<>(data);
        copy.sort(Collections.reverseOrder());

        double top = 0.0, sum = 0.0;
        for (double d : copy) {
            sum += d;
        }
        for (int i = 0; i < Math.min(k, copy.size()); i++) {
            top += copy.get(i);
        }

        return top / (sum + 1e-6);
    }
}
