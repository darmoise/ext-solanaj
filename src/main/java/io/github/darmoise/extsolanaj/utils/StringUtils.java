package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
