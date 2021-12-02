package com.hackathon.resource;

import com.hackathon.model.Release;
import com.hackathon.model.Status;
import com.hackathon.model.Ticket;
import com.hackathon.model.TicketRelease;
import com.hackathon.model.TicketUtil;
import com.hackathon.model.Timeline;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("/timeline")
@Produces("application/json")
public class ReleaseResource {

    List<LocalDate> holidayList = Arrays.asList(LocalDate.of(2021, 12, 2));

    @POST
    @Path("/release/{ticketId}")
    @Consumes("application/json")
    public Release getReleaseDetails(List<Update> updateList, @PathParam("ticketId") String ticketId) throws Exception {
        TicketRelease ticketRelease = getRelease(updateList, ticketId);
        Release.ReleaseBuilder release = Release.builder().ticketRelease(ticketRelease);

        LocalDate startDate = getEndDate(ticketRelease.getEndDate(), 1); // Startdate of first event (EG: Soft code freeze)

        List<Timeline> timelineList = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : TicketUtil.getTimeline().entrySet()) {
            timelineList.add(Timeline.builder().name(entry.getKey()).date(startDate).build());
            startDate = getEndDate(startDate, entry.getValue());
        }
        release.timelineList(timelineList);
        return release.build();
    }


    @GET
    @Path("/release/{ticketId}")
    public Ticket getTicketDetails(@PathParam("ticketId") String ticketId) {
        return TicketUtil.populateTicketInfo(ticketId);
    }

    private TicketRelease getRelease(List<Update> updateList, String ticketId) throws Exception {
        TicketUtil.updateTicketFields(ticketId, updateList); // TODO
        Ticket ticket = TicketUtil.getTicket(ticketId);
        TicketRelease ticketRelease = TicketRelease.builder().startDate(ticket.getCreatedDate()).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();
        getSla(ticket, ticketRelease, new HashMap<>());
        return ticketRelease;
    }

    private int getSla(Ticket ticketInfo, TicketRelease ticketRelease, HashMap<String, Status> visited) throws Exception {
        Status visitedStatus = visited.get(ticketInfo.getKey());
        if (visitedStatus == Status.VISITED) {
            return 0;
        } else if (visitedStatus == Status.VISITING) {
            throw new Exception("VISITING");
        } else {
            visited.put(ticketInfo.getKey(), Status.VISITING);

            int maxDependsOn = 0;
            int maxSubTask = 0;

            int ticketCount = 0;
            for (Ticket ticket : ticketInfo.getDepends_on()) {
                TicketRelease dependOnRelease = TicketRelease.builder().startDate(ticketRelease.getStartDate()).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();
                maxDependsOn = Math.max(maxDependsOn, getSla(ticket, dependOnRelease, visited));
                System.out.println(ticketRelease.getDepends_on());
                ticketRelease.getDepends_on().add(dependOnRelease);
                ticketCount += ticketRelease.getTicketCount();
            }

            System.out.println(maxDependsOn);

            for (Ticket ticket : ticketInfo.getSubTask()) {
                TicketRelease subTaskRelease = TicketRelease.builder().startDate(getEndDate(ticketRelease.getStartDate(), maxDependsOn)).ticket(ticket).subTask(new ArrayList<>()).depends_on(new ArrayList<>()).build();

                maxSubTask = Math.max(maxSubTask, getSla(ticket, subTaskRelease, visited));
                ticketRelease.getSubTask().add(subTaskRelease);
                ticketCount += ticketRelease.getTicketCount();
            }
            visited.put(ticketInfo.getKey(), Status.VISITED);

            int days = maxDependsOn + maxSubTask + ticketInfo.getSla();
            LocalDate endDate = getEndDate(ticketRelease.getStartDate(), days);
            ticketRelease.setEndDate(endDate);
            ticketRelease.setTicketCount(ticketCount + 1);
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
