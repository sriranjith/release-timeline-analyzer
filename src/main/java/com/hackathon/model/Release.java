package com.hackathon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Release {
    Ticket ticket;
    List<Release> depends_on = new ArrayList<>();
    List<Release> subTask = new ArrayList<>();

    LocalDate startDate;
    LocalDate endDate;
    int ticketCount;
    int completedTicketCount;
}

/*
* Ticket-> Ticketid, startdate, endDate, subTask, depends_on
*
* tickerid -> obj
*
* */
