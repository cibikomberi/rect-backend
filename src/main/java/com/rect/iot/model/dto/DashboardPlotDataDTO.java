package com.rect.iot.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class DashboardPlotDataDTO {
    String type;
    List<ChartDataDTO> data;
}
