package com.hackathon.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketUtil {

    private static Map<String, Ticket> map = new HashMap<>();

    public static Ticket populateTicketInfo(String ticketId) {
        // Actual jira API call and JSON parsing.
        Ticket t3 = new Ticket("3", Arrays.asList(), Arrays.asList(), 1, LocalDate.now());
        Ticket t4 = new Ticket("4", Arrays.asList(), Arrays.asList(), 1, LocalDate.now());
        Ticket t5 = new Ticket("5", Arrays.asList(), Arrays.asList(), 5, LocalDate.now());
        Ticket t2 = new Ticket("2", Arrays.asList(t5), Arrays.asList(), 3, LocalDate.now());
        Ticket t1 = new Ticket("1", Arrays.asList(), Arrays.asList(t5), 2, LocalDate.now());
        map.put("1", t1);
        map.put("2", t2);
        map.put("3", t3);
        map.put("4", t4);
        map.put("5", t5);
        return map.get(ticketId);
    }

    public static Ticket getTicket(String ticketId) {
        return map.get(ticketId);
    }

    public static void updateTicketFields(String ticketId, List<Update> updateList) {
        for (Update update : updateList) {
            Ticket ticket = map.get(ticketId);
            if (ticket == null) {
                // populateTicketInfo(); // TODO
                return;
            }
            switch (update.getFieldName()) {
                case "sla":
                    ticket.setSla(Integer.parseInt(update.getFieldValue()));
                    break;
                default:
                    break;
            }
        }
    }
}
