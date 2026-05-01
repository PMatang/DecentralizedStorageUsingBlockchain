package com.decstorage.service;

import org.json.JSONObject;

import com.decstorage.model.User;

public class ABACPolicyEvaluator {

    public static boolean evaluate(User user, String policyJson) {
        try {
            if (policyJson == null || policyJson.trim().isEmpty()
                    || policyJson.trim().equals("{}")) {
                return true; // no policy = open access
            }

            JSONObject policy = new JSONObject(policyJson);

            if (policy.has("role")) {
                String required = policy.getString("role");
                if (!required.equalsIgnoreCase(user.getRole())) {
                    return false;
                }
            }

            if (policy.has("department")) {
                String required = policy.getString("department");
                if (!required.equalsIgnoreCase(user.getDepartment())) {
                    return false;
                }
            }

            if (policy.has("clearance")) {
                String required = policy.getString("clearance");
                if (!meetsLevel(user.getClearanceLevel(), required)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean meetsLevel(String userLevel, String required) {
        return levelScore(userLevel) >= levelScore(required);
    }

    private static int levelScore(String level) {
        if (level == null) return 0;
        switch (level.toLowerCase()) {
            case "high":   return 3;
            case "medium": return 2;
            case "low":    return 1;
            default:       return 0;
        }
    }
}