package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate begin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate end;
}
