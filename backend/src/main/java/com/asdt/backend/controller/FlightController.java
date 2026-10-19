package com.asdt.backend.controller;

import com.asdt.backend.model.Detection;
import com.asdt.backend.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flights;

    public FlightController(FlightService flights) {
        this.flights = flights;
    }

    @GetMapping("/active")
    public Map<String, List<Detection>> active() {
        return flights.findActiveFlights();
    }
}