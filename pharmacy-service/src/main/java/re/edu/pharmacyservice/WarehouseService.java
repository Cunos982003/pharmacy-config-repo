package re.edu.pharmacyservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

    /**
     * Simulates calling warehouse service to check inventory.
     * In a real scenario, this would make an HTTP request or call a remote service.
     */
    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "checkWarehouseFallback")
    public String checkInventory(String medicineId) {
        // Simulate a call that always fails (throws exception) to trigger circuit breaker
        throw new RuntimeException("Warehouse service is down");
    }

    // Fallback method when circuit breaker is open
    public String checkWarehouseFallback(String medicineId, Exception e) {
        return "Không thể kết nối kho tổng. Hệ thống sẽ sử dụng dữ liệu tồn kho cục bộ để tiếp tục giao dịch";
    }
}