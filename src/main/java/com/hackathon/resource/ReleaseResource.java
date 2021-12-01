package com.hackathon.resource;


import com.hackathon.model.Release;
import com.hackathon.model.Status;
import com.hackathon.model.Ticket;
import com.hackathon.model.TicketUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Path("/timeline")
@Produces({"application/json"})
public class ReleaseResource {

    @GET
    @Path("/release/{ticketId}")
    public Release getRelease(@PathParam("ticketId") String ticketId) throws Exception {
        Ticket ticket = getTicketInfo(ticketId);
        int sla = getSla(ticket) - 1;
        System.out.println(sla);
        LocalDate createdDate = ticket.getCreatedDate();
        LocalDate endEndDate = getEndDate(createdDate, sla);
        Release release = new Release();
        release.setStart(createdDate);
        release.setEnd(endEndDate);
        return release;
    }

    public LocalDate getEndDate(LocalDate date, int workdays) {
        if (workdays < 1) {
            return date;
        }

        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < workdays) {
            result = result.plusDays(1);
            if (!isSkipDay(result)) {
                addedDays++;
            }
        }

        return result;
    }

    List<LocalDate> holidayList = Arrays.asList(LocalDate.of(2021, 12, 2));

    private boolean isSkipDay(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY || holidayList.contains(date);
    }

    private Ticket getTicketInfo(String ticketId) {
        return TicketUtil.getTicket("1");
    }

    private int getSla(Ticket ticketInfo) throws Exception {
        if (ticketInfo.getStatus() == Status.VISITING) {
            throw new Exception("VISITING");
        }
        if (ticketInfo.getStatus() == Status.VISITED) {
            return 0;
        }
        ticketInfo.setStatus(Status.VISITING);

        int maxDependsOn = 0;
        int maxSubTask = 0;

        for (Ticket ticket : ticketInfo.getDepends_on()) {
            maxDependsOn = Math.max(maxDependsOn, getSla(ticket));
        }

        for (Ticket ticket : ticketInfo.getSubTask()) {
            maxSubTask = Math.max(maxSubTask, getSla(ticket));
        }
        ticketInfo.setStatus(Status.VISITED);
        return maxDependsOn + maxSubTask + ticketInfo.getSla();
    }
}
