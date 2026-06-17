package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {
    public static void addAll(List<String> list, Object newItems) {
        if (newItems instanceof List<?> l) {
            l.forEach(x -> list.add(String.valueOf(x)));
        }
    }
}
