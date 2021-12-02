package com.hackathon.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class Timeline {
    @JsonProperty
    String name;
    @JsonProperty
    LocalDate date;
}
