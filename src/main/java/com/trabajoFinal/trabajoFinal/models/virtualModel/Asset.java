package com.trabajoFinal.trabajoFinal.models.virtualModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Asset {

    private byte[] content;
    private String contentType;
}
