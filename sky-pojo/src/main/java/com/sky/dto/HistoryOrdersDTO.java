package com.sky.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class HistoryOrdersDTO implements Serializable {

    private int page;

    private int pageSize;

    private Integer status;

    private Long userId;
}
