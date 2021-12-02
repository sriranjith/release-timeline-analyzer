package com.hackathon.model;

import lombok.Data;

@Data
public class Update {
    String ticketId;
    String fieldName;
    String fieldValue;
}
