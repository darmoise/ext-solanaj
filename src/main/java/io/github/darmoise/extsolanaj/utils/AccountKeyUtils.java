package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountKeyUtils {
    public static final String METHOD_GET_LOADED_ADDRESSES = "getLoadedAddresses";
    public static final String METHOD_GET_WRITABLE = "getWritable";
    public static final String METHOD_GET_READONLY = "getReadonly";
    public static final String FIELD_WRITABLE = "writable";
    public static final String FIELD_READONLY = "readonly";

    @SuppressWarnings("unchecked")
    public static List<String> extractMerged(ConfirmedTransaction tx) {
        val keys = new ArrayList<>(tx.getTransaction().getMessage().getAccountKeys());
        val meta = tx.getMeta();

        if (meta == null) {
            return keys;
        }

        val loaded = tryInvoke(meta, METHOD_GET_LOADED_ADDRESSES);

        if (loaded instanceof Map<?, ?> map) {
            addFromMapField(keys, map, FIELD_WRITABLE);
            addFromMapField(keys, map, FIELD_READONLY);
        } else if (loaded != null) {
            addFromUnknownList(keys, tryInvoke(loaded, METHOD_GET_WRITABLE));
            addFromUnknownList(keys, tryInvoke(loaded, METHOD_GET_READONLY));
        }
        return keys;
    }

    private static void addFromMapField(List<String> into, Map<?, ?> map, String fieldName) {
        if (map == null) {
            return;
        }
        addFromUnknownList(into, map.get(fieldName));
    }

    private static void addFromUnknownList(List<String> into, Object listObj) {
        if (listObj instanceof List<?> list) {
            for (Object x : list) {
                into.add(String.valueOf(x));
            }
        }
    }

    private static Object tryInvoke(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            val m = target.getClass().getMethod(methodName);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Throwable ignore) {
            return null;
        }
    }
}
