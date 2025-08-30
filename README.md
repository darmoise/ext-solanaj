# SolanaJ helper

Utilities for **SolanaJ** focused on parsing **Metaplex Core (MPL Core)** NFT transfers.

* detects **incoming** Core transfers (`transfer`, `transfer_v1`) to a given wallet;
* works with **v0 transactions** (merges Address Lookup Tables `loadedAddresses`);
* extracts Memo (v1/v2) as `reference`;
* minimal deps, Java 17+.

> This is **not** for SPL tokens. MPL Core is a separate program (no ATAs).

## Installation
### Option 1: GitHub Packages
```xml
<!-- pom.xml -->
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
  <groupId>io.github.darmoise</groupId>
  <artifactId>solanaj-helper</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Requirements
* Java **17+**;
* SolanaJ `1.23.0+`;
* Transitive deps: `bitcoinj-core` (Base58), Lombok (compileOnly).

## Quick start

```java
import io.github.darmoise.extsolanaj.core.MplCoreTransferExtractor;
import io.github.darmoise.extsolanaj.model.Transfer;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;

import java.util.Optional;

public class Example {
    private static final String MPL_CORE_ID = "%YOU_MPL_CORE_ID%";

    public static void main(String[] args) throws Exception {
        var rpc = new RpcClient(Cluster.MAINNET);
        var myWallet = new PublicKey("%YOU_ADDRESS%");
        var signature = "%YOU_SIGNATURE%";

        ConfirmedTransaction tx = rpc.getApi()
            .getTransaction(signature);

        var extractor = new MplCoreTransferExtractor(MPL_CORE_ID);
        Optional<Transfer> incoming = extractor.extractTransfer(tx, myWallet, signature);

        incoming.ifPresent(t -> {
            System.out.println("asset = " + t.getNftAddress());
            System.out.println("from = " + t.getSender());
            System.out.println("to = " + t.getRecipient());
            System.out.println("memo = " + t.getMemo());
            System.out.println("sig = " + t.getSignature());
        });
    }
}
```