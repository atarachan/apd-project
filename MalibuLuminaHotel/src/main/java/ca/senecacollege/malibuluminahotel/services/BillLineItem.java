package ca.senecacollege.malibuluminahotel.services;

import java.math.BigDecimal;

public record BillLineItem(
        String description,
        String calculation,
        BigDecimal amount) {
}
