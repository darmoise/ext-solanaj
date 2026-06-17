package io.github.darmoise.extsolanaj.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParsedString {
    private String value;
    private int nextOffset;
}
