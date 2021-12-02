# ticketRelease-timeline-analyzer
 mvn clean install
<br>java -jar target/releasetimelineanalyzer-1.0-SNAPSHOT.jar server

API:
<br>GET /timeline/ticketRelease/1
<br>POST /timeline/ticketRelease/1
[
{
    "ticketId": "1",
    "fieldName": "sla",
    "fieldValue": "100"
}
]
