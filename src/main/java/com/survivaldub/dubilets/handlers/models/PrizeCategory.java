package com.survivaldub.dubilets.handlers.models;

public enum PrizeCategory {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    LEGENDARY("legendary");

    private final String category;

    PrizeCategory(String category) {
        this.category = category;
    }

    public static PrizeCategory get(String value) {
        switch (value.toLowerCase()) {
            case "uncommon":
                return UNCOMMON;
            case "rare":
                return RARE;
            case "legendary":
                return LEGENDARY;
        }
        return COMMON;
    }

    public String getString() {
        return this.category;
    }
}
