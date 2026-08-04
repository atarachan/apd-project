package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.*;
import ca.senecacollege.malibuluminahotel.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for generating various reports.
 */
public class ReportService {

    private final IReservationRepository reservationRepository;
    private final IBillRepository billRepository;
    private final IRoomRepository roomRepository;
    private final IActivityLogRepository activityLogRepository;
    private final IFeedbackRepository feedbackRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportService() {
        this.reservationRepository = new ReservationRepositoryImpl();
        this.billRepository = new BillRepositoryImpl();
        this.roomRepository = new RoomRepositoryImpl();
        this.activityLogRepository = new ActivityLogRepositoryImpl();
        this.feedbackRepository = new FeedbackRepositoryImpl();
    }

    /**
     * Generate revenue report for a date range.
     *
     * @param startDate Start date
     * @param endDate   End date
     * @return List of revenue data by date
     */
    public List<Map<String, String>> generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, String>> reportData = new ArrayList<>();
        
        // Get all bills in date range
        List<Bill> bills = billRepository.findAll().stream()
                .filter(bill -> {
                    LocalDate billDate = bill.getBillDate().toLocalDate();
                    return !billDate.isBefore(startDate) && !billDate.isAfter(endDate);
                })
                .toList();

        // Group by date and calculate totals
        Map<LocalDate, RevenueData> revenueByDate = new HashMap<>();
        for (Bill bill : bills) {
            LocalDate billDate = bill.getBillDate().toLocalDate();
            revenueByDate.putIfAbsent(billDate, new RevenueData());
            
            RevenueData data = revenueByDate.get(billDate);
            data.count++;
            data.subtotal = data.subtotal.add(bill.getSubtotal());
            data.tax = data.tax.add(bill.getTax());
            data.total = data.total.add(bill.getTotal());
        }

        // Convert to report format
        for (Map.Entry<LocalDate, RevenueData> entry : revenueByDate.entrySet()) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("Date", entry.getKey().format(DATE_FORMATTER));
            row.put("Reservations", String.valueOf(entry.getValue().count));
            row.put("Subtotal", String.format("$%.2f", entry.getValue().subtotal));
            row.put("Tax", String.format("$%.2f", entry.getValue().tax));
            row.put("Total Revenue", String.format("$%.2f", entry.getValue().total));
            reportData.add(row);
        }

        return reportData;
    }

    /**
     * Generate occupancy report for a date range.
     *
     * @param startDate Start date
     * @param endDate   End date
     * @return List of occupancy data by date
     */
    public List<Map<String, String>> generateOccupancyReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, String>> reportData = new ArrayList<>();
        
        List<Room> allRooms = roomRepository.findAll();
        int totalRooms = allRooms.size();

        // For each date in range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            final LocalDate date = currentDate;
            
            // Count occupied rooms
            long occupiedCount = reservationRepository.findAll().stream()
                    .filter(res -> {
                        LocalDate checkIn = res.getCheckInDate();
                        LocalDate checkOut = res.getCheckOutDate();
                        return !date.isBefore(checkIn) && date.isBefore(checkOut);
                    })
                    .count();

            double occupancyRate = (totalRooms > 0) ? (occupiedCount * 100.0 / totalRooms) : 0.0;

            Map<String, String> row = new LinkedHashMap<>();
            row.put("Date", date.format(DATE_FORMATTER));
            row.put("Total Rooms", String.valueOf(totalRooms));
            row.put("Occupied", String.valueOf(occupiedCount));
            row.put("Available", String.valueOf(totalRooms - occupiedCount));
            row.put("Occupancy Rate", String.format("%.1f%%", occupancyRate));
            reportData.add(row);

            currentDate = currentDate.plusDays(1);
        }

        return reportData;
    }

    /**
     * Generate activity log report for a date range.
     *
     * @param startDate Start date
     * @param endDate   End date
     * @return List of activity log entries
     */
    public List<Map<String, String>> generateActivityLogReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, String>> reportData = new ArrayList<>();
        
        List<ActivityLog> logs = activityLogRepository.findByTimestampBetween(
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
        );

        for (ActivityLog log : logs) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("Timestamp", log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            row.put("Admin", log.getAdminUser() != null ? log.getAdminUser().getUsername() : "System");
            row.put("Action", log.getAction());
            row.put("Entity", log.getEntity());
            row.put("Entity ID", String.valueOf(log.getEntityId()));
            row.put("Description", log.getMessage());
            reportData.add(row);
        }

        return reportData;
    }

    /**
     * Generate feedback summary.
     *
     * @return Summary statistics for feedback
     */
    public Map<String, Object> generateFeedbackSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        
        List<Feedback> allFeedback = feedbackRepository.findAll();
        
        summary.put("Total Feedback", allFeedback.size());
        
        if (!allFeedback.isEmpty()) {
            Double avgRating = feedbackRepository.getAverageRating();
            summary.put("Average Rating", String.format("%.2f", avgRating != null ? avgRating : 0.0));
            
            // Count by rating
            for (int i = 5; i >= 1; i--) {
                final int rating = i;
                long count = allFeedback.stream().filter(f -> f.getRating() == rating).count();
                summary.put(rating + " Stars", count);
            }
        } else {
            summary.put("Average Rating", "N/A");
        }
        
        return summary;
    }

    /**
     * Inner class to hold revenue data.
     */
    private static class RevenueData {
        int count = 0;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
    }
}
