package ca.senecacollege.malibuluminahotel.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IReportService {

    List<Map<String, String>> generateRevenueReport(LocalDate startDate, LocalDate endDate);

    List<Map<String, String>> generateOccupancyReport(LocalDate startDate, LocalDate endDate);

    List<Map<String, String>> generateActivityLogReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> generateFeedbackSummary();
}
