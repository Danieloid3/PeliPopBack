package bd.pelipop.Controllers;

import bd.pelipop.Services.HealthCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/keep-alive")
public class KeepAliveController {
    private final HealthCheckService service;

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveController.class);

    @Autowired
    public KeepAliveController(HealthCheckService service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping(@RequestParam(required = false) String ts) {

        Map<String, String> health = service.check();
        logger.info("Keep-alive ping received at {}: {}", ts != null ? ts : "unknown time", health);

        return ResponseEntity.ok(health);
    }
}