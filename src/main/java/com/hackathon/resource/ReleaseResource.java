package com.hackathon.resource;

import com.hackathon.model.Release;
import com.hackathon.model.Status;
import com.hackathon.model.Ticket;
import com.hackathon.model.TicketUtil;
import com.hackathon.model.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/timeline")
@Produces("application/json")
public class ReleaseResource {

    List<LocalDate> holidayList = Arrays.asList(LocalDate.of(2021, 12, 2));

    @GET
    @Path("/release/{ticketId}")
    public Ticket getTicketDetails(@PathParam("ticketId") String ticketId) {
        return TicketUtil.populateTicketInfo(ticketId);
    }

    @POST
    @Path("/release/{ticketId}")
    @Consumes("application/json")
    public Release getRelease(List<Update> updateList, @PathParam("ticketId") String ticketId) throws Exception {
        TicketUtil.updateTicketFields(ticketId, updateList);
        Ticket ticket = TicketUtil.getTicket(ticketId);
        return getRelease(ticket);
    }

    private Release getRelease(Ticket ticket) throws Exception {
        Release release = Release.builder().startDate(ticket.getCreatedDate()).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();
        int sla = getSla(ticket, release, new HashMap<>());
        System.out.println(sla);
        return release;
    }

    private int getSla(Ticket ticketInfo, Release release, HashMap<String, Status> visited) throws Exception {
            switch (visited.get(ticketInfo.getKey())) {
                case VISITING:
                    throw new Exception("VISITING");
                case VISITED:
                    return 0;
                default:
                    visited.put(ticketInfo.getKey(), Status.VISITING);

                    int maxDependsOn = 0;
                    int maxSubTask = 0;

                    int ticketCount = 0;
                    for (Ticket ticket : ticketInfo.getDepends_on()) {
                        Release dependOnRelease = Release.builder().startDate(release.getStartDate()).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();
                        maxDependsOn = Math.max(maxDependsOn, getSla(ticket, dependOnRelease, visited));
                        System.out.println(release.getDepends_on());
                        release.getDepends_on().add(dependOnRelease);
                        ticketCount += release.getTicketCount();
                    }

                    System.out.println(maxDependsOn);

                    for (Ticket ticket : ticketInfo.getSubTask()) {
                        Release subTaskRelease = Release.builder().startDate(getEndDate(release.getStartDate(), maxDependsOn)).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();

                        maxSubTask = Math.max(maxSubTask, getSla(ticket, subTaskRelease, visited));
                        release.getSubTask().add(subTaskRelease);
                        ticketCount += release.getTicketCount();
                    }
                    visited.put(ticketInfo.getKey(), Status.VISITED);

                    int days = maxDependsOn + maxSubTask + ticketInfo.getSla();
                    LocalDate endDate = getEndDate(release.getStartDate(), days);
                    release.setEndDate(endDate);
                    release.setTicketCount(ticketCount + 1);
                    return days;
            }
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

    private boolean isSkipDay(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY || holidayList.contains(date);
    }
}
