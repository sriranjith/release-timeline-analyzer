package com.hackathon.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TicketUtil {

    private static Map<String, Ticket> map = new HashMap<>();

    static {
        Ticket t3 = new Ticket("3", Arrays.asList(), Arrays.asList(), 1, LocalDate.now(), Status.NONE);
        Ticket t4 = new Ticket("4", Arrays.asList(), Arrays.asList(), 1, LocalDate.now(), Status.NONE);
        Ticket t5 = new Ticket("5", Arrays.asList(), Arrays.asList(), 5, LocalDate.now(), Status.NONE);
        Ticket t2 = new Ticket("2", Arrays.asList(t5), Arrays.asList(), 3, LocalDate.now(), Status.NONE);
        Ticket t1 = new Ticket("1", Arrays.asList(), Arrays.asList(t5), 2, LocalDate.now(), Status.NONE);

        map.put("1", t1);
        map.put("2", t2);
        map.put("3", t3);
    }

    public static Ticket getTicket(String ticketId) {
        return map.get(ticketId);
    }
}
