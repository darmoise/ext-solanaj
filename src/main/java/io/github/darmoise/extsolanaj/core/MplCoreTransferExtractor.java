package io.github.darmoise.extsolanaj.core;

import io.github.darmoise.extsolanaj.model.Transfer;
import io.github.darmoise.extsolanaj.utils.ExtBase58;
import io.github.darmoise.extsolanaj.utils.NumberUtils;
import io.github.darmoise.extsolanaj.utils.StringUtils;
import lombok.val;
import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static io.github.darmoise.extsolanaj.core.Programs.isMemoV1;
import static io.github.darmoise.extsolanaj.core.Programs.isMemoV2;
import static io.github.darmoise.extsolanaj.core.Programs.isMplCore;
import static io.github.darmoise.extsolanaj.utils.AccountKeyUtils.extractMerged;
import static io.github.darmoise.extsolanaj.utils.MplUtils.isTransfer;
import static io.github.darmoise.extsolanaj.utils.MplUtils.isTransferV1;

public class MplCoreTransferExtractor {
    public static Optional<Transfer> extractTransfer(
        final ConfirmedTransaction tx,
        final String signature,
        final PublicKey publicKey
    ) {
        if (tx.getTransaction() == null) {
            return Optional.empty();
        }

        val message = tx.getTransaction().getMessage();
        val keys = extractMerged(tx);
        val memo = extractMemoIfAny(message.getInstructions(), keys);

        for (val instr : message.getInstructions()) {
            val programId = getProgramId(instr, keys);
            if (!isMplCore(programId)) {
                continue;
            }

            val data = ExtBase58.decode(instr.getData());
            val isV1 = isTransferV1(data);
            val isV0 = !isV1 && isTransfer(data);

            if (!isV0 && !isV1) {
                continue;
            }

            val acc = instr.getAccounts().stream()
                .map(NumberUtils::toInt)
                .toList();

            val assetIndex = 0;
            val toIndex = isV1 ? 4 : 2;
            val fromIndex = isV1 ? 2 : 1;

            if (!hasPositions(acc, assetIndex, toIndex, fromIndex)) {
                continue;
            }

            val asset = keyAt(keys, acc, assetIndex);
            val to = keyAt(keys, acc, toIndex);
            val from = keyAt(keys, acc, fromIndex);

            if (asset == null || to == null) {
                continue;
            }

            if (!publicKey.toBase58().equals(to)) {
                continue;
            }

            return Optional.of(Transfer.builder()
                .signature(signature)
                .nftAddress(asset)
                .sender(from)
                .recipient(to)
                .amount(1)
                .reference(memo)
                .build());
        }
        return Optional.empty();
    }

    private static String extractMemoIfAny(List<ConfirmedTransaction.Instruction> ixs, List<String> keys) {
        for (var i : ixs) {
            String pid = getProgramId(i, keys);
            if (!isMemoV1(pid) && !isMemoV2(pid)) {
                continue;
            }

            try {
                return new String(Base58.decode(StringUtils.nullToEmpty(i.getData())), StandardCharsets.UTF_8);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static String getProgramId(
        final ConfirmedTransaction.Instruction instruction,
        final List<String> keys
    ) {
        val index = Long.valueOf(instruction.getProgramIdIndex()).intValue();
        return keys.get(index);
    }

    private static boolean hasPositions(List<Integer> acc, int... pos) {
        for (int p : pos) if (p < 0 || p >= acc.size()) return false;
        return true;
    }

    private static String keyAt(List<String> keys, List<Integer> acc, int pos) {
        if (!hasPositions(acc, pos)) return null;
        int k = acc.get(pos);
        if (k < 0 || k >= keys.size()) return null;
        return keys.get(k);
    }
}
