package io.github.darmoise.extsolanaj.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MplUtils {

    public static final byte[] IX_TRANSFER = disc("transfer");
    public static final byte[] IX_TRANSFER_V1 = disc("transfer_v1");

    private static final String DISC_ALGORITHM = "SHA-256";

    public static boolean isTransfer(byte[] data) {
        return ByteUtils.startsWith(data, IX_TRANSFER) || (data != null && data.length > 0 && data[0] == 13);
    }

    public static boolean isTransferV1(byte[] data) {
        return ByteUtils.startsWith(data, IX_TRANSFER_V1) || (data != null && data.length > 0 && data[0] == 14);
    }

    private static byte[] disc(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance(DISC_ALGORITHM);
            md.update(("global:" + name).getBytes(StandardCharsets.UTF_8));
            return Arrays.copyOf(md.digest(), 8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
