package com.asdt.backend.repository;

import com.asdt.backend.model.Detection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DetectionRepository extends MongoRepository<Detection, String> {
}
