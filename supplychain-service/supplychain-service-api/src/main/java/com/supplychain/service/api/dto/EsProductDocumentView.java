package com.supplychain.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsProductDocumentView implements Serializable {

    private Integer id;

    private String name;

    private String brand;

    private Double price;

    private String createdAt;
}
