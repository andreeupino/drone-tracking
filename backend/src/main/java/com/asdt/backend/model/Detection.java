package com.asdt.backend.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("detections")
@CompoundIndex(name = "sn_timestamp", def = "{'sn': 1, 'timestamp': 1}")
public class Detection {
    @Id
    private String id;

    @NotBlank
    @Size(max = 64)
    private String sn;                 // identifica al dron

    @Indexed(expireAfter = "7d")
    private Instant timestamp;

    @Valid @NotNull
    private Location droneLocation;
    @Valid
    private Location driverLocation;
    @Valid
    private Location homeLocation;

    @Size(max = 100) private String name;
    @Size(max = 100) private String uid;
    @Size(max = 100) private String flightID;
    @Size(max = 100) private String flightPurpose;
    @Size(max = 100) private String mac;
    @Size(max = 100) private String operatorId;
    @Size(max = 100) private String productType;
    @Size(max = 100) private String vendor;

    private Double pitch, roll, vx, vy, vz;
    private Integer productId, reliability, signalPower, signalType;

    @Data
    public static class Location {
        @NotNull @DecimalMin("-90") @DecimalMax("90")
        private Double lat;
        @NotNull @DecimalMin("-180") @DecimalMax("180")
        private Double lon;
        private Double distance, aHeight, fHeight;
    }
}