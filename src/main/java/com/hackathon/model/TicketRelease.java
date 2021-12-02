package com.hackathon.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class TicketRelease {
    Ticket ticket;
    List<TicketRelease> depends_on = new ArrayList<>();
    List<TicketRelease> subTask = new ArrayList<>();

    LocalDate startDate;
    LocalDate endDate;
    int ticketCount;
    int completedTicketCount;
}