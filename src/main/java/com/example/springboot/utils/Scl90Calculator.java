package com.example.springboot.utils;

import com.example.springboot.entity.Scl90Assessment;
import com.example.springboot.entity.StudentEvaluateResult;
import java.util.List;

public class Scl90Calculator {

    public static StudentEvaluateResult calculate(Scl90Assessment assessment) {
        List<Integer> scores = assessment.getAnswers(); // ✅ 改为 List
        if (scores == null || scores.size() != 90) {
            throw new IllegalArgumentException("answers 列表必须有 90 个元素");
        }

        StudentEvaluateResult result = new StudentEvaluateResult();

        // ---------- 1. 总分 ----------
        int totalScore = 0;
        for (Integer s : scores) {
            totalScore += (s != null ? s : 0);
        }
        result.setTotalScore(totalScore);
        result.setTotalAvg(totalScore / 90.0);

        // 阳性项目数（>=2）
        int positiveItemNum = 0;
        int positiveSum = 0;
        for (Integer s : scores) {
            if (s != null && s >= 2) {
                positiveItemNum++;
                positiveSum += s;
            }
        }
        result.setPositiveItemNum(positiveItemNum);
        result.setPositiveAvg(positiveItemNum > 0 ? positiveSum / (double) positiveItemNum : 0.0);

        // ---------- 2. 各因子分 ----------
        int[] somatizationIdx = {0,3,11,26,39,41,47,48,51,52,55,57};
        int[] obsessiveIdx     = {2,8,9,27,37,44,45,50,54,64};
        int[] interpersonalIdx = {5,20,33,35,36,40,60,68,72};
        int[] depressionIdx    = {4,13,14,19,21,25,28,29,30,31,53,70,78};
        int[] anxietyIdx       = {1,16,22,32,38,56,71,77,79,85};
        int[] hostilityIdx     = {10,23,62,66,73,80};
        int[] phobicIdx        = {12,24,46,49,69,74,81};
        int[] paranoidIdx      = {7,17,42,67,75,82};
        int[] psychoticIdx     = {6,15,34,61,76,83,84,86,87,88};
        int[] otherIdx         = {18,43,58,59,63,65,88};

        result.setSomatizationScore(sumByIndex(scores, somatizationIdx));
        result.setObsessiveScore(sumByIndex(scores, obsessiveIdx));
        result.setInterpersonalScore(sumByIndex(scores, interpersonalIdx));
        result.setDepressionScore(sumByIndex(scores, depressionIdx));
        result.setAnxietyScore(sumByIndex(scores, anxietyIdx));
        result.setHostilityScore(sumByIndex(scores, hostilityIdx));
        result.setPhobicScore(sumByIndex(scores, phobicIdx));
        result.setParanoidScore(sumByIndex(scores, paranoidIdx));
        result.setPsychoticScore(sumByIndex(scores, psychoticIdx));
        result.setOtherScore(sumByIndex(scores, otherIdx));

        return result;
    }

    private static int sumByIndex(List<Integer> scores, int[] idxs) {
        int sum = 0;
        for (int i : idxs) {
            if (i >= 0 && i < scores.size() && scores.get(i) != null) {
                sum += scores.get(i);
            }
        }
        return sum;
    }
}
