package org.hackathon.timeline.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class Ticket {

    String ticketId;
    List<Ticket> depends_on = new ArrayList<>();
    List<Ticket> subTask = new ArrayList<>();
    int sla;
    LocalDate createdDate;
}
