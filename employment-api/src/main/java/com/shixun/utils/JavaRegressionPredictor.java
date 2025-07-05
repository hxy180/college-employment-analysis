package com.shixun.utils;

import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;
import smile.math.MathEx;
import smile.regression.LinearModel;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class JavaRegressionPredictor {

    public static List<Double> predict(List<Integer> xList, List<Double> yList, int forecastYears) {
        int n = xList.size();
        if (n < 2) return new ArrayList<>();

        DataFrame data = buildDataFrame(xList, yList);
        Formula formula = Formula.lhs("y");
        LinearModel model = smile.regression.OLS.fit(formula, data);

        List<Double> forecasts = new ArrayList<>();
        int lastYear = xList.get(n - 1);

        for (int i = 1; i <= forecastYears; i++) {
            DataFrame predictionData = DataFrame.of(IntVector.of("x", new int[]{lastYear + i}));
            double[] predictions = model.predict(predictionData);
            forecasts.add(predictions[0]);
        }

        return forecasts;
    }

    public static int calculateConfidence(List<Integer> xList, List<Double> yList, LinearModel model) {
        int n = xList.size();
        if (n < 2) return 0;

        // 计算模型对历史数据的预测值
        double[] historicalPredictions = new double[n];
        for (int i = 0; i < n; i++) {
            DataFrame point = DataFrame.of(IntVector.of("x", IntStream.of(xList.get(i))));
            historicalPredictions[i] = model.predict(point)[0];
        }

        // 计算残差和RMSE
        double[] residuals = new double[n];
        for (int i = 0; i < n; i++) {
            residuals[i] = yList.get(i) - historicalPredictions[i];
        }

        double sumSquared = 0;
        for (double r : residuals) {
            sumSquared += r * r;
        }
        double rmse = Math.sqrt(sumSquared / n);

        // 计算变异系数
        double meanY = MathEx.mean(yList.stream().mapToDouble(Double::doubleValue).toArray());
        if (Math.abs(meanY) < 1e-10) {
            return 50; // 防止除零错误
        }

        double cv = rmse / meanY;

        // 将CV转换为置信度（范围50-95）
        return (int) Math.min(95, Math.max(50, 100 - (cv * 100)));
    }

    public static DataFrame buildDataFrame(List<Integer> xList, List<Double> yList) {
        IntVector xVector = IntVector.of("x", xList.stream().mapToInt(Integer::intValue).toArray());
        DoubleVector yVector = DoubleVector.of("y", yList.stream().mapToDouble(Double::doubleValue).toArray());
        return DataFrame.of(xVector, yVector);
    }
}