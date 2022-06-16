package org.rostovpavel.base.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArraysFormat {

    public static int generateHistorySum(List<BigDecimal> data, BigDecimal item) {
        List<Integer> historyDiff = getHistoryDiffFromCorrectData(data, item);
        return historyDiff.stream().mapToInt(i -> i).sum();
    }

    public static int generateHistorySum(List<String> data, String item, String target) {
        List<Integer> historyDiff = getHistoryDiffFromCorrectData(data, item, target);
        return historyDiff.stream().mapToInt(i -> i).sum();
    }

    public static int generateHistorySum(List<BigDecimal> data, BigDecimal item, boolean isUp) {
        List<Integer> historyDiff = getHistoryDiffFromCorrectDataUpLine(data, item);
        return isUp ? historyDiff.stream().mapToInt(i -> i).sum() : 0;
    }

    private static List<Integer> getHistoryDiffFromCorrectData(List<BigDecimal> data, BigDecimal item) {
        List<Integer> res = new ArrayList<>();
        res.add(getPoint(item, data.get(0)));
        res.add(getPoint(data.get(0), data.get(1)));
        res.add(getPoint(data.get(1), data.get(2)));
        return res;
    }

    private static List<Integer> getHistoryDiffFromCorrectData(List<String> data, String item, String target) {
        List<Integer> res = new ArrayList<>();
        res.add(getPoint(item, target));
        res.add(getPoint(data.get(0), target));
        res.add(getPoint(data.get(1), target));
        return res;
    }

    private static List<Integer> getHistoryDiffFromCorrectDataUpLine(List<BigDecimal> data, BigDecimal item) {
        List<Integer> res = new ArrayList<>();
        res.add(getPointUp(item, data.get(0)));
        res.add(getPointUp(data.get(0), data.get(1)));
        res.add(getPointUp(data.get(1), data.get(2)));
        return res;
    }

    private static int getPoint(BigDecimal A, BigDecimal B) {
        return Integer.compare(A.compareTo(B), 0);
    }

    private static int getPoint(String A, String target) {
        return A.equals(target) ? 1 : -1;
    }

    private static int getPointUp(BigDecimal A, BigDecimal B) {
        if (A.compareTo(BigDecimal.valueOf(0)) > 0) {
            return A.compareTo(B) > 0 ? 1 : 0;
        }
        return A.compareTo(B) < 0 ? -1 : 0;
    }
}
