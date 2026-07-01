package re.edu.pharmacyservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class InsuranceService {

    // Simple in-memory price list for demonstration (medicineId -> price per unit in VND)
    private static final Map<String, Double> MEDICINE_PRICES = new HashMap<>();
    static {
        MEDICINE_PRICES.put("PARACETAMOL", 10000.0);
        MEDICINE_PRICES.put("AMOXICILLIN", 25000.0);
        MEDICINE_PRICES.put("OMEPRAZOLE", 30000.0);
        // Add more as needed
    }

    /**
     * Verifies insurance for a medicine and returns the final price after insurance.
     * This method is protected by Timeout, Retry, and CircuitBreaker.
     *
     * @param medicineId the ID of the medicine
     * @param quantity   the quantity of the medicine
     * @return a string representing the result (insurance price or fallback message)
     */
    @TimeLimiter(name = "insuranceServiceTimelimiter", fallbackMethod = "verifyInsuranceFallback")
    @Retry(name = "insuranceServiceRetry", fallbackMethod = "verifyInsuranceFallback")
    @CircuitBreaker(name = "insuranceCB", fallbackMethod = "verifyInsuranceFallback")
    public CompletableFuture<String> verifyInsurance(String medicineId, int quantity) {
        // Simulate a call to insurance service that might be slow or fail
        // In a real scenario, this would be an HTTP call to the insurance server.
        return CompletableFuture.supplyAsync(() -> {
            // Simulate network delay or failure for demonstration
            // Uncomment the line below to simulate a delay longer than timeout (e.g., 4000ms) to test timeout
            // try { Thread.sleep(4000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Simulate occasional failure to test retry and circuit breaker
            // Uncomment the line below to simulate a failure (e.g., throw exception) for testing
            // if (Math.random() < 0.7) { // 70% chance of failure
            //     throw new RuntimeException("Insurance service timeout or error");
            // }

            // If we get here, assume insurance verification succeeded
            // In reality, we would get a discount rate from the insurance service
            double discountRate = 0.2; // 20% discount as example
            double undiscountedPrice = getUndiscountedPrice(medicineId, quantity);
            double discountedPrice = undiscountedPrice * (1 - discountRate);

            return String.format("Giá sau bảo hiểm: %.0f VND (Giá gốc: %.0f VND, Giảm %.0f%%).",
                    discountedPrice, undiscountedPrice, discountRate * 100);
        });
    }

    /**
     * Fallback method when timeout, retry exhausted, or circuit breaker is open.
     * Returns the undiscounted price with a note.
     *
     * @param medicineId the ID of the medicine
     * @param quantity   the quantity of the medicine
     * @param throwable  the exception that caused the fallback (if any)
     * @return a string with the undiscounted price and note
     */
    public String verifyInsuranceFallback(String medicineId, int quantity, Throwable throwable) {
        double undiscountedPrice = getUndiscountedPrice(medicineId, quantity);
        return String.format("Giá thuốc chưa chiết khấu: %.0f VND. Ghi chú: Xác thực bảo hiểm sau.", undiscountedPrice);
    }

    /**
     * Helper method to get the undiscounted price for a medicine and quantity.
     *
     * @param medicineId the ID of the medicine
     * @param quantity   the quantity
     * @return the undiscounted price in VND
     */
    private double getUndiscountedPrice(String medicineId, int quantity) {
        Double pricePerUnit = MEDICINE_PRICES.getOrDefault(medicineId, 0.0);
        return pricePerUnit * quantity;
    }
}