package com.survivaldub.dubilets.utils;

import java.util.*;

public class MathUtils {

    private static final Random random = new Random();

    public static Random getRandom() {
        return random;
    }

    public static <T> T getRandomFromArray(T[] array) {
        if (array.length == 0) return null;
        return array[random.nextInt(array.length)];
    }

    public static <T> T getRandomFromList(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T getRandomFromCollection(Collection<T> collection) {
        int pos = random.nextInt(collection.size());
        for (T t : collection) {
            if (--pos < 0) return t;
        }
        return null;
    }

    public static <T> List<T> getUniqueRandomElements(Collection<T> collection, int total) {
        ArrayList<T> newList = new ArrayList<>(total);
        ArrayList<T> finalList = new ArrayList<>(collection);
        for (int i = 0; i < total; ++i) {
            int size = finalList.size();
            if (size <= 0) break;
            int randomIndex = random.nextInt(size);
            newList.add(finalList.get(randomIndex));
            finalList.remove(randomIndex);
        }
        return newList;
    }

    public static int getRandomNumber(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }

    public static double getRandomNumber(double min, double max) {
        return random.nextInt((int) (max + 1.0 - min)) + min;
    }
}
