package com.hackathon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class Ticket {
    String key;
    List<Ticket> depends_on = new ArrayList<>();
    List<Ticket> subTask = new ArrayList<>();
    int sla;
    LocalDate createdDate;
    //String status;
}
