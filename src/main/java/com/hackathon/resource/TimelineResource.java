package org.hackathon.timeline.resource;

import org.hackathon.timeline.model.Release;
import org.hackathon.timeline.model.Ticket;
import org.hackathon.timeline.model.TicketUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@Path("/timeline")
@Produces({"application/json"})
public class TimelineResource {
    Map<Ticket, Set<Ticket>> ticketDependency;

    public TimelineResource() {
        ticketDependency = new HashMap<>();
    }

    @GET
    @Path("/release")
    public Release calculateReleaseInfo(@QueryParam("ticketId") String ticketId) throws Exception {
        Ticket ticket = TicketUtil.getTicket(ticketId);
        ticketDependency.put(ticket, new HashSet<>());
        populateTicketInfo(ticket);
        System.out.println(ticketDependency);
        int days = runTopologicalSort();
        System.out.println("days = " + days);
        LocalDate startDate = ticket.getCreatedDate();
        LocalDate endDate = startDate.plusDays(getAllDays(startDate.getDayOfWeek().getValue(), days));

        return new Release(ticket.getCreatedDate(), endDate);
    }

    private void populateTicketInfo(Ticket ticketInfo) {
        for (Ticket ticket : ticketInfo.getDepends_on()) {
            ticketDependency.putIfAbsent(ticket, new HashSet<>());
            ticketDependency.get(ticket).add(ticketInfo);
            populateTicketInfo(ticket);
        }

        for (Ticket ticket : ticketInfo.getSubTask()) {
            ticketDependency.putIfAbsent(ticket, new HashSet<>());
            ticketDependency.get(ticket).add(ticketInfo);
            populateTicketInfo(ticket);
        }
    }

    private int runTopologicalSort() throws Exception {
        Map<Ticket, Integer> indegree = new HashMap<>();

        for (Ticket ticket : ticketDependency.keySet()) {
            indegree.put(ticket, 0);
        }

        for (Map.Entry<Ticket, Set<Ticket>> entry : ticketDependency.entrySet()) {
            for (Ticket ticket : entry.getValue()) {
                indegree.put(ticket, indegree.get(ticket) + 1);
            }
        }

        Queue<Object[]> q = new PriorityQueue<>((a, b) -> ((Ticket) a[0]).getSla() - ((Ticket) b[0]).getSla());

        for (Ticket ticket : ticketDependency.keySet()) {
            if (indegree.get(ticket) == 0) {
                q.add(new Object[]{ticket, ticket.getSla()});
            }
        }

        if (q.size() == 0) {
            throw new Exception("Ticket cycle detected");
        }
        int totalTime = 0;

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                Object[] obj = q.poll();
                Ticket temp = ((Ticket) obj[0]);
                int timeSoFar = ((int) obj[1]);
                totalTime = Math.max(totalTime, timeSoFar);
                for (Ticket nei : ticketDependency.get(temp)) {
                    indegree.put(nei, indegree.get(nei) - 1);
                    if (indegree.get(nei) == 0) {
                        q.add(new Object[]{nei, nei.getSla() + timeSoFar});
                    }
                }
            }
        }
        return totalTime;
    }

    public static long getAllDays(int dayOfWeek, long businessDays) {
        long result = 0;
        if (businessDays != 0) {
            boolean isStartOnWorkday = dayOfWeek < 6;
            long absBusinessDays = Math.abs(businessDays);

            if (isStartOnWorkday) {
                // if negative businessDays: count backwards by shifting weekday
                int shiftedWorkday = businessDays > 0 ? dayOfWeek : 6 - dayOfWeek;
                result = absBusinessDays + (absBusinessDays + shiftedWorkday - 1) / 5 * 2;
            } else { // start on weekend
                // if negative businessDays: count backwards by shifting weekday
                int shiftedWeekend = businessDays > 0 ? dayOfWeek : 13 - dayOfWeek;
                result = absBusinessDays + (absBusinessDays - 1) / 5 * 2 + (7 - shiftedWeekend);
            }
        }
        return result;
    }
}
