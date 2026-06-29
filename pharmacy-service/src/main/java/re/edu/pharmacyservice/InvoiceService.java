package re.edu.pharmacyservice;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    /**
     * Creates a new electronic invoice.
     * This method is protected by Retry and RateLimiter to handle transient network errors
     * and prevent excessive requests from clients.
     */
    @Retry(name = "invoiceServiceRetry", fallbackMethod = "invoiceFallback")
    @RateLimiter(name = "invoiceRateLimiter", fallbackMethod = "invoiceFallback")
    public String createInvoice(String invoiceData) {
        // Simulate a call to external invoice service or database operation
        // In a real scenario, this might involve network calls that can fail transiently
        throw new RuntimeException("Temporary network error when creating invoice");
    }

    // Fallback method when retry is exhausted or rate limit is exceeded
    public String invoiceFallback(String invoiceData, Throwable throwable) {
        // Return a quick response instead of letting the request hang
        return "Invoice processing temporarily unavailable. Please try again later.";
    }
}