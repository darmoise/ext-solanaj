package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtils {
    public static int toInt(Object n) {
        if (n instanceof Integer i) {
            return i;
        }
        if (n instanceof Long l) {
            return l.intValue();
        }
        if (n instanceof Double d) {
            return d.intValue();
        }
        if (n instanceof Number num) {
            return num.intValue();
        }

        throw new IllegalArgumentException("Unsupported number type: " + (n == null ? "null" : n.getClass()));
    }

    public static List<Integer> toIntList(Object arr) {
        if (!(arr instanceof List<?> list)) return List.of();
        ArrayList<Integer> out = new ArrayList<>(list.size());
        for (Object x : list) {
            out.add(toInt(x));
        }
        return out;
    }
}
