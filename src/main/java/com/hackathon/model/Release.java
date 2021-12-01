package com.hackathon.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Release {
    LocalDate start;
    LocalDate end;
}
