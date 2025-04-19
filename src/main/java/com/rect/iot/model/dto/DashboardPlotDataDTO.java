package com.rect.iot.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardPlotDataDTO {
    String type;
    List<ChartDataDTO> data;
}
