package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bitcoinj.core.Base58;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtBase58 {
    public static byte[] decode(final String data) {
        return Base58.decode(StringUtils.nullToEmpty(data));
    }
}
