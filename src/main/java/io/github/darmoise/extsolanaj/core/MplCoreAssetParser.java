package io.github.darmoise.extsolanaj.core;

import io.github.darmoise.extsolanaj.model.ParsedString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.PublicKey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Parses the raw account data of an MPL Core V1 asset.
 *
 * BaseAssetV1 layout (Borsh):
 *   [0]      key (1 byte)
 *   [1..32]  owner (32 bytes)
 *   [33]     update_authority variant: 0=None, 1=Address, 2=Collection
 *   [34..65] update_authority pubkey (32 bytes, only if variant != 0)
 *   [?]      name_len (4 bytes LE) + name bytes
 *   [?]      uri_len  (4 bytes LE) + uri bytes
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MplCoreAssetParser {

    private static final int UPDATE_AUTHORITY_OFFSET = 33;
    private static final int PUBKEY_SIZE             = 32;
    private static final int U32_SIZE                = 4;
    private static final int CREATE_V1_DISCRIMINATOR = 0;
    private static final int DATA_STATE_ACCOUNT      = 0;

    /**
     * Extracts the name field from raw MPL Core asset account data.
     *
     * @param data raw account bytes (base64-decoded)
     * @return NFT name
     * @throws IllegalArgumentException if data is too short or malformed
     */
    public static String parseName(final byte[] data) {
        return parse(data).getName();
    }

    /**
     * Parses all base asset fields needed to validate a mint-on-withdraw result.
     *
     * <p>This method expects raw MPL Core asset account data returned by RPC {@code getAccountInfo}, not
     * {@link org.p2p.solanaj.core.TransactionInstruction#getData()} bytes. Instruction data only contains serialized
     * instruction arguments and cannot be used to validate owner or collection membership.</p>
     *
     * @param data raw account bytes (base64-decoded)
     * @return parsed MPL Core asset DTO
     * @throws IllegalArgumentException if data is too short or malformed
     */
    public static MplCoreAsset parse(final byte[] data) {
        checkBounds(data, 0, 1 + PUBKEY_SIZE + 1);
        rejectInstructionData(data);

        val key = data[0] & 0xFF;
        val owner = publicKey(data, 1);
        int offset = UPDATE_AUTHORITY_OFFSET;

        val variant = data[offset] & 0xFF;
        offset += 1;

        PublicKey updateAuthority = null;
        if (variant == MplCoreAsset.UPDATE_AUTHORITY_ADDRESS
            || variant == MplCoreAsset.UPDATE_AUTHORITY_COLLECTION) {
            updateAuthority = publicKey(data, offset);
            offset += PUBKEY_SIZE;
        } else if (variant != MplCoreAsset.UPDATE_AUTHORITY_NONE) {
            throw new IllegalArgumentException("Unsupported MPL Core update authority variant: " + variant);
        }

        val name = readString(data, offset);
        offset = name.getNextOffset();
        val uri = readString(data, offset);

        return MplCoreAsset.builder()
            .key(key)
            .owner(owner)
            .updateAuthorityVariant(variant)
            .updateAuthority(updateAuthority)
            .name(name.getValue())
            .uri(uri.getValue())
            .build();
    }

    private static ParsedString readString(final byte[] data, final int offset) {
        checkBounds(data, offset, U32_SIZE);
        val len = ByteBuffer.wrap(data, offset, U32_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt();
        if (len < 0) {
            throw new IllegalArgumentException("Negative MPL Core string length: " + len);
        }
        val stringOffset = offset + U32_SIZE;
        checkBounds(data, stringOffset, len);
        return new ParsedString(new String(data, stringOffset, len, StandardCharsets.UTF_8), stringOffset + len);
    }

    private static void rejectInstructionData(final byte[] data) {
        if ((data[0] & 0xFF) == CREATE_V1_DISCRIMINATOR
            && data.length > 1
            && (data[1] & 0xFF) == DATA_STATE_ACCOUNT) {
            throw new IllegalArgumentException(
                "MPL Core asset parser expects raw asset account data from getAccountInfo, "
                    + "but the supplied bytes look like createV1 instruction data"
            );
        }
    }

    private static PublicKey publicKey(final byte[] data, final int offset) {
        checkBounds(data, offset, PUBKEY_SIZE);
        val bytes = new byte[PUBKEY_SIZE];
        System.arraycopy(data, offset, bytes, 0, PUBKEY_SIZE);
        return new PublicKey(Base58.encode(bytes));
    }

    private static void checkBounds(final byte[] data, final int offset, final int need) {
        if (data.length < offset + need) {
            throw new IllegalArgumentException(
                "MPL Core asset data too short: need " + (offset + need) + " bytes, got " + data.length
            );
        }
    }
}