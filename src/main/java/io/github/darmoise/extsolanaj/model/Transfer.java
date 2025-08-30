package io.github.darmoise.extsolanaj.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transfer {
    private String sender;
    private String recipient;
    private String nftAddress;
    private String reference;
    private String signature;
    private long amount;
}
