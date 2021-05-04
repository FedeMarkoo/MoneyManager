package com.fedeMarkoo.prueba.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document
public class BitsoData {
    private List<Balance> data;
    private Instant instant;

}
