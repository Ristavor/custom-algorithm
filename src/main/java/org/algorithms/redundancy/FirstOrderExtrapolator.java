package org.algorithms.redundancy;

import com.example.common.annotations.AlgorithmName;
import com.example.common.model.IAlgorithm;

import java.util.HashMap;
import java.util.Map;

@AlgorithmName("FirstOrderExtrapolator")
public class FirstOrderExtrapolator implements IAlgorithm {
    private double[] timeStamps;
    private double[] timeSeries;
    private double tolerance;

    public FirstOrderExtrapolator(double[] timeStamps, double[] timeSeries, double tolerance) {
        this.timeStamps = timeStamps;
        this.timeSeries = timeSeries;
        this.tolerance = tolerance;
    }

    public FirstOrderExtrapolator() {
    }

    public double[][] filter() {
        if (timeStamps == null || timeSeries == null || timeStamps.length != timeSeries.length || timeStamps.length == 0) {
            throw new IllegalArgumentException("Invalid input data.");
        }

        int n = timeStamps.length;
        boolean[] valid = new boolean[n];
        valid[0] = true; // The first measurement is always considered valid
        int lastValidIndex = 0;

        for (int i = 1; i < n - 1; i++) {
            double x0 = timeStamps[lastValidIndex];
            double y0 = timeSeries[lastValidIndex];
            double x1 = timeStamps[lastValidIndex + 1];
            double y1 = timeSeries[lastValidIndex + 1];

            // Calculate the parameters of the line y = a * x + b
            double a = (y1 - y0) / (x1 - x0);
            double b = y0 - a * x0;

            boolean isSignificant = false;

            // Check if the current point is within the tolerance band
            double yInterp = a * timeStamps[i] + b;
            if (Math.abs(yInterp - timeSeries[i]) > tolerance) {
                isSignificant = true;
            }

            if (isSignificant) {
                valid[i] = true;
                lastValidIndex = i;
            } else {
                valid[i] = false;
            }
        }
        valid[n - 1] = true; // The last measurement is always considered valid

        // Collecting valid measurements
        int validCount = 0;
        for (boolean v : valid) {
            if (v) validCount++;
        }

        double[] filteredTimeSeries = new double[validCount];
        double[] filteredTimeStamps = new double[validCount];
        int index = 0;
        for (int i = 0; i < n; i++) {
            if (valid[i]) {
                filteredTimeSeries[index] = timeSeries[i];
                filteredTimeStamps[index] = timeStamps[i];
                index++;
            }
        }

        double[][] filteredResults = new double[2][validCount];
        filteredResults[0] = filteredTimeStamps;
        filteredResults[1] = filteredTimeSeries;

        return filteredResults;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        if (parameters.containsKey("tolerance")) {
            this.tolerance = Double.parseDouble(parameters.get("tolerance"));
        }
    }

    @Override
    public void setInput(String inputData) {
        // Предполагаем, что inputData будет в формате "timestamps:timeSeries"
        String[] parts = inputData.split(";");
        String[] timestampStrings = parts[0].split(",");
        String[] timeSeriesStrings = parts[1].split(",");

        this.timeStamps = new double[timestampStrings.length];
        this.timeSeries = new double[timeSeriesStrings.length];

        for (int i = 0; i < timestampStrings.length; i++) {
            this.timeStamps[i] = Double.parseDouble(timestampStrings[i]);
        }

        for (int i = 0; i < timeSeriesStrings.length; i++) {
            this.timeSeries[i] = Double.parseDouble(timeSeriesStrings[i]);
        }
    }

    @Override
    public String solve() {
        double[][] result = filter();
        StringBuilder resultString = new StringBuilder();

        resultString.append("Timestamps:");
        for (double timestamp : result[0]) {
            resultString.append(timestamp).append(",");
        }
        resultString.setLength(resultString.length() - 1); // Удаляем последнюю запятую

        resultString.append(" | TimeSeries:");
        for (double value : result[1]) {
            resultString.append(value).append(",");
        }
        resultString.setLength(resultString.length() - 1); // Удаляем последнюю запятую

        return resultString.toString();
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("tolerance", "Tolerance value for filtering");
        return params;
    }

    public double[] getTimeStamps() {
        return timeStamps;
    }

    public double[] getTimeSeries() {
        return timeSeries;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTimeSeries(double[] timeSeries) {
        this.timeSeries = timeSeries;
    }

    public void setTimeStamps(double[] timeStamps) {
        this.timeStamps = timeStamps;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}
