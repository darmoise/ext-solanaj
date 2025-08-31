package io.github.darmoise.extsolanaj.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transfer {
    private String sender;
    private String recipient;
    private String assetAddress;
    private String collection;
    private String signature;
    private String memo;
    private long amount;
}
