package trspo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import trspo.dto.BookDTO;
import trspo.dto.FlightDTO;

import java.text.ParseException;
import java.util.UUID;

public class Main{
    private static final String URL = "http://localhost:8088";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final HttpHeaders headers = new HttpHeaders();
    private static final HttpEntity<Object> headersEntity = new HttpEntity<>(headers);

    public static void main(String[] args) throws ParseException {
        headers.setContentType(MediaType.APPLICATION_JSON);
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.arrivalDate = "Oct 22, 2000, 4:35:00 PM";
        flightDTO.arriving = "New-York";
        flightDTO.departureDate = "Oct 22, 2000, 4:30:00 PM";
        flightDTO.departing = "Paris";
        flightDTO.checkInOpens = "Oct 22, 2000, 2:35:00 PM";
        flightDTO.status = "On-time";
        flightDTO.businessClassSeat = 20;
        flightDTO.firstClassSeat = 20;
        flightDTO.economClassSeat = 30;
        flightDTO.businessClassPrice = 120;
        flightDTO.firstClassPrice = 90;
        flightDTO.economClassPrice = 70;

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String flightJsonStr = gson.toJson(flightDTO);

        // create flight
        HttpEntity<String> flightJson = new HttpEntity<>(flightJsonStr, headers);
        //restTemplate.exchange(URL + "/flights/add",  HttpMethod.POST, flightJson, Void.class);

        // create client
        HttpEntity<Void> client = new HttpEntity<>(headers);
        String name = "Kate";
        String surName = "Ivanova";
        ResponseEntity<UUID> response_client = restTemplate.exchange(URL + "/client/newOne?name="+ name +"&surname="+ surName,  HttpMethod.POST, client, UUID.class);
        UUID clientId = response_client.getBody();
        // get flights
        ResponseEntity<String> response = restTemplate.getForEntity(URL + "/info/allFlights", String.class);
        JsonArray flightDTOs = gson.fromJson(response.getBody(), JsonArray.class);
        System.out.println(flightDTOs);
        FlightDTO flightDTO1 = gson.fromJson(flightDTOs.get(0), FlightDTO.class);
        UUID someFlightId = flightDTO1.id;

        // book_ticket
        BookDTO bookDTO = new BookDTO();
        bookDTO.sitClass = "Business";
        bookDTO.clientId = clientId;
        bookDTO.flightId  = someFlightId;
        bookDTO.payed = false;
        String bookString = gson.toJson(bookDTO);
        HttpEntity<String> bookJson = new HttpEntity<>(bookString, headers);
        ResponseEntity<String> response_ticket = restTemplate.exchange(URL + "/booking",  HttpMethod.POST, bookJson, String.class);
        String ticketId = response_ticket.getBody();
        System.out.println("Ticket: "+ ticketId);

        // checkIn
        HttpEntity<Void> passenger = new HttpEntity<>(headers);
        ResponseEntity<String> response_passenger = restTemplate.exchange(URL + "/check_in?ticket_id="+ ticketId,  HttpMethod.POST, passenger, String.class);
        String passengerId = response_passenger.getBody();
        System.out.println(passengerId);

        // baggage checkIn
        HttpEntity<Void> baggage = new HttpEntity<>(headers);
        float weight = 8.2f;
        restTemplate.exchange(URL + "/checkInBaggage?id="+ passengerId+"&weight=" + Float.toString(weight),  HttpMethod.POST, baggage, Void.class);

        // flight control
        HttpEntity<Void> controll = new HttpEntity<>(headers);
        restTemplate.exchange(URL + "/flightControl?id="+ passengerId,  HttpMethod.POST, controll, Void.class);

        // custom control(Red)
        String description = "10000$ and diamonds";
        restTemplate.exchange(URL + "/customControl/redLine?id="+ passengerId + "&description=" + description,  HttpMethod.POST, controll, Void.class);

        // border control
        restTemplate.exchange(URL + "/borderControl?id="+ passengerId,  HttpMethod.POST, controll, Void.class);

        // get baggage
        restTemplate.exchange(URL + "/getMyBaggage?id="+ passengerId,  HttpMethod.POST, controll, Void.class);
    }
}
