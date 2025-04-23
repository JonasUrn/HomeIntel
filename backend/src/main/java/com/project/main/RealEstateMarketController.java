package com.project.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/housing-market")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "false")
public class RealEstateMarketController {

    private final RealEstateMarketService realEstateMarketService;

    @Autowired
    public RealEstateMarketController(RealEstateMarketService realEstateMarketService) {
        this.realEstateMarketService = realEstateMarketService;
    }

    @GetMapping("/{zipCode}")
    public ResponseEntity<?> getHousingMarketData(@PathVariable String zipCode) {
        try {
            // Always get national data regardless of input parameter
            Map<String, Object> data = realEstateMarketService.getHousingMarketData("United States");
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Add a default endpoint for when no zipCode is provided
    @GetMapping
    public ResponseEntity<?> getNationalHousingMarketData() {
        try {
            Map<String, Object> data = realEstateMarketService.getHousingMarketData("United States");
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}