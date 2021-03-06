package com.hackathon.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Release {
    @JsonProperty
    TicketRelease ticketRelease;

    @JsonProperty
    List<Timeline> timelineList;
}
