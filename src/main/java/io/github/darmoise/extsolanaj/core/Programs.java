package io.github.darmoise.extsolanaj.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.p2p.solanaj.core.PublicKey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Programs {
    public static final String MPL_CORE = "CoREENxT6tW1HoK8ypY1SxRMZTcVPm7R94rH4PZNhX7d";
    public static final String MEMO_V1 = "Memo1UhkJRfHyvLMcVucJwxXeuD728EqVDDwQDxFMNo";
    public static final String MEMO_V2 = "MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr";
    public static final String LOG_WRAPPER = "noopb9bkMVfRPU8AsbpTUg8AQkHtKwMYZiFUjNRtMmV";

    public static final PublicKey PK_MPL_CORE = new PublicKey(MPL_CORE);
    public static final PublicKey PK_MEMO_V1= new PublicKey(MEMO_V1);
    public static final PublicKey PK_MEMO_V2= new PublicKey(MEMO_V2);
    public static final PublicKey PK_LOG_WRAPPER= new PublicKey(LOG_WRAPPER);

    public static boolean isMplCore(String programId) {
        return MPL_CORE.equals(programId);
    }

    public static boolean isMemoV1(String programId) {
        return MEMO_V1.equals(programId);
    }

    public static boolean isMemoV2(String programId) {
        return MEMO_V2.equals(programId);
    }
}
