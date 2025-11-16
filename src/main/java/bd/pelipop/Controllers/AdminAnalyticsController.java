package bd.pelipop.Controllers;

import bd.pelipop.DTO.AnalyticsSummary;
import bd.pelipop.Services.AnalyticsETLService;
import bd.pelipop.Services.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pelipop/admin/analytics")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsETLService etlService;

    public AdminAnalyticsController(AnalyticsService analyticsService,
                                    AnalyticsETLService etlService) {
        this.analyticsService = analyticsService;
        this.etlService = etlService;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> triggerSync() {
        etlService.syncUsersToMongo();
        return ResponseEntity.ok("ETL ejecutado");
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummary> summary() {
        return ResponseEntity.ok(analyticsService.buildSummary());
    }
}
