package re.edu.pharmacyservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

    /**
     * Simulates calling warehouse service to check inventory.
     * In a real scenario, this would make an HTTP request or call a remote service.
     * For demonstration, we'll simulate failures.
     */
    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "warehouseFallback")
    public String checkInventory(String medicineId) {
        // Simulate a call that always fails (throws exception) to trigger circuit breaker
        throw new RuntimeException("Warehouse service is down");
    }

    // Fallback method when circuit breaker is open
    public String warehouseFallback(String medicineId, Throwable throwable) {
        return "Inventory service unavailable (circuit breaker open). Fallback response.";
    }
}