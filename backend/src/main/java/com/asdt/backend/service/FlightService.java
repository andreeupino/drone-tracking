package com.asdt.backend.service;

import com.asdt.backend.model.Detection;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FlightService {

    private static final Duration ACTIVE_WINDOW = Duration.ofMinutes(2);
    private static final Duration FLIGHT_GAP = Duration.ofMinutes(2);
    private static final int MAX_POINTS_PER_FLIGHT = 5000;

    private final MongoTemplate mongo;

    public FlightService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public Map<String, List<Detection>> findActiveFlights() {
        Instant since = Instant.now().minus(ACTIVE_WINDOW);
        List<String> sns = mongo.findDistinct(
                Query.query(Criteria.where("timestamp").gte(since)),
                "sn", Detection.class, String.class);

        Map<String, List<Detection>> result = new LinkedHashMap<>();
        for (String sn : sns) {
            result.put(sn, currentFlight(sn));
        }
        return result;
    }

    private List<Detection> currentFlight(String sn) {
        Query q = Query.query(Criteria.where("sn").is(sn))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(MAX_POINTS_PER_FLIGHT);

        List<Detection> flight = new ArrayList<>();
        try (Stream<Detection> stream = mongo.stream(q, Detection.class)) {
            Iterator<Detection> it = stream.iterator();
            Instant newer = null;
            while (it.hasNext()) {
                Detection d = it.next();
                if (newer != null
                        && Duration.between(d.getTimestamp(), newer).compareTo(FLIGHT_GAP) > 0) {
                    break; 
                }
                flight.add(d);
                newer = d.getTimestamp();
            }
        }
        Collections.reverse(flight);
        return flight;
    }
}