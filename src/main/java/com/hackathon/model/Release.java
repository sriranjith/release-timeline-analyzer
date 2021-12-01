package org.hackathon.timeline.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Release {
    LocalDate start;
    LocalDate end;
}
