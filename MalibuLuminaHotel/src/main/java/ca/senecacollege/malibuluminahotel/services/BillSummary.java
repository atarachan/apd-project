package ca.senecacollege.malibuluminahotel.services;

import java.math.BigDecimal;
import java.util.List;

public record BillSummary(
        BigDecimal roomTotal,
        BigDecimal addOnTotal,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        long nights,
        List<BillLineItem> lineItems) {
}
