package io.github.darmoise.extsolanaj.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static io.github.darmoise.extsolanaj.core.Programs.PK_LOG_WRAPPER;
import static io.github.darmoise.extsolanaj.core.Programs.PK_MPL_CORE;
import static io.github.darmoise.extsolanaj.core.Programs.PK_SYSTEM_PROGRAM;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MplCoreMintProgram {

    private static final byte CREATE_V1_DISCRIMINATOR = 0x00;
    private static final byte DATA_STATE_ACCOUNT      = 0x00;
    private static final byte OPTION_NONE             = 0x00;
    private static final byte OPTION_SOME             = 0x01;
    private static final int U32_BYTES = 4;

    /**
     * Builds an MPL Core createV1 instruction.
     *
     * <p>When {@code collection} is not {@code null}, the collection account is passed as a writable account and
     * {@code collectionAuthority} signs as the authority. The update authority optional account stays unset; MPL Core
     * derives the collection update-authority relationship from the collection account and authority signer.</p>
     *
     * <p>When {@code collection} is {@code null}, the MPL Core program id is used as the optional-account sentinel
     * for collection/updateAuthority and the asset is created standalone.</p>
     */
    public static TransactionInstruction createV1(
        final PublicKey payer,
        final PublicKey asset,
        final PublicKey collection,
        final PublicKey collectionAuthority,
        final PublicKey owner,
        final String name,
        final String uri
    ) {
        val hasCollection = collection != null;
        val authority = hasCollection ? collectionAuthority : PK_MPL_CORE;

        val metas = new ArrayList<AccountMeta>();
        metas.add(new AccountMeta(asset, true, true));
        metas.add(new AccountMeta(hasCollection ? collection : PK_MPL_CORE, false, hasCollection));
        metas.add(new AccountMeta(authority, hasCollection, false));
        metas.add(new AccountMeta(payer, true, true));
        metas.add(new AccountMeta(owner, false, false));
        metas.add(new AccountMeta(PK_MPL_CORE, false, false));
        metas.add(new AccountMeta(PK_SYSTEM_PROGRAM, false, false));
        metas.add(new AccountMeta(PK_LOG_WRAPPER, false, false));

        return new TransactionInstruction(PK_MPL_CORE, metas, buildCreateData(name, uri));
    }

    private static byte[] buildCreateData(final String name, final String uri) {
        val nameBytes = name.getBytes(StandardCharsets.UTF_8);
        val uriBytes = uri.getBytes(StandardCharsets.UTF_8);
        val buf = ByteBuffer.allocate(1 + 1 + 4 + nameBytes.length + 4 + uriBytes.length + 5);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        buf.put(CREATE_V1_DISCRIMINATOR);
        buf.put(DATA_STATE_ACCOUNT);
        putString(buf, nameBytes);
        putString(buf, uriBytes);
        buf.put(OPTION_SOME);
        buf.putInt(0);
        return buf.array();
    }

    private static void putOptionalString(final ByteBuffer buf, final byte[] value) {
        if (value == null) {
            buf.put(OPTION_NONE);
            return;
        }
        buf.put(OPTION_SOME);
        putString(buf, value);
    }

    private static void putString(final ByteBuffer buf, final byte[] value) {
        buf.putInt(value.length);
        buf.put(value);
    }
}