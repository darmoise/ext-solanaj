package io.github.darmoise.extsolanaj.core;

import lombok.Builder;
import lombok.Data;
import org.p2p.solanaj.core.PublicKey;

@Data
@Builder
public class MplCoreAsset {
    public static final int UPDATE_AUTHORITY_NONE = 0;
    public static final int UPDATE_AUTHORITY_ADDRESS = 1;
    public static final int UPDATE_AUTHORITY_COLLECTION = 2;

    private int key;
    private PublicKey owner;
    private int updateAuthorityVariant;
    private PublicKey updateAuthority;
    private String name;
    private String uri;

    /**
     * MPL Core does not store a separate Token Metadata-style "verified" boolean for collections.
     * An asset is considered a collection asset when its update authority enum is the Collection variant.
     */
    public boolean hasCollection() {
        return updateAuthorityVariant == UPDATE_AUTHORITY_COLLECTION && updateAuthority != null;
    }

    /**
     * @return true when this asset has a plain address update authority and is not a collection asset
     */
    public boolean hasAddressUpdateAuthority() {
        return updateAuthorityVariant == UPDATE_AUTHORITY_ADDRESS && updateAuthority != null;
    }

    /**
     * @return true when this asset has no update authority
     */
    public boolean hasNoUpdateAuthority() {
        return updateAuthorityVariant == UPDATE_AUTHORITY_NONE;
    }

    public PublicKey collection() {
        return hasCollection() ? updateAuthority : null;
    }

    /**
     * Convenience check for post-mint validation and wallet/indexer diagnostics.
     *
     * @param expectedCollection collection account expected by the caller
     * @return true when this asset uses UpdateAuthority::Collection(expectedCollection)
     */
    public boolean isInCollection(final PublicKey expectedCollection) {
        return expectedCollection != null && hasCollection() && expectedCollection.equals(updateAuthority);
    }
}