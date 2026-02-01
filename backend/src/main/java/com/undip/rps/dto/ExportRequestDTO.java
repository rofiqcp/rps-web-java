package com.undip.rps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequestDTO {
    private RPSDataDTO rpsData;
    private Map<String, Object> meta;
}
