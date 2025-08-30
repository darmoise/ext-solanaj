package io.github.darmoise.extsolanaj.core;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.programs.SystemProgram;

import java.util.ArrayList;

import static io.github.darmoise.extsolanaj.core.Programs.PK_LOG_WRAPPER;
import static io.github.darmoise.extsolanaj.core.Programs.PK_MPL_CORE;
import static io.github.darmoise.extsolanaj.utils.ByteUtils.concat;
import static io.github.darmoise.extsolanaj.utils.MplUtils.IX_TRANSFER_V1;

@Slf4j
public class MplCoreTransferProgram {
    public static TransactionInstruction createV1(
        final PublicKey fundingAccount,
        final PublicKey walletAddress,
        final PublicKey assetAddress,
        final PublicKey collectionAddress
    ) {
        val metas = new ArrayList<AccountMeta>();

        metas.add(new AccountMeta(assetAddress, false, true));   // 0: asset (writable)
        if (collectionAddress != null) {
            metas.add(new AccountMeta(collectionAddress, false, false)); // 1: collection (opt)
        }
        metas.add(new AccountMeta(fundingAccount, true, true));   // 2: payer (signer)
        metas.add(new AccountMeta(PK_MPL_CORE, false, false));  // 3: authority (program)
        metas.add(new AccountMeta(walletAddress, false, false));  // 4: new_owner
        metas.add(new AccountMeta(SystemProgram.PROGRAM_ID, false, false)); // 5: system program
        metas.add(new AccountMeta(PK_LOG_WRAPPER, false, false));  // 6: log wrapper

        byte[] data = concat(IX_TRANSFER_V1, new byte[] { 0x00 });

        return new TransactionInstruction(PK_MPL_CORE, metas, data);
    }
}
