package re.edu.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RefreshScope
public class BillController {

    @Value("${pharmacy.vat-rate}")
    private double vatRate;

    @PostMapping("/bill")
    public Map<String, Object> calculateBill(@RequestBody Map<String, Object> request) {
        double medicineAmount = ((Number) request.getOrDefault("medicineAmount", 0.0)).doubleValue();

        double vatAmount = medicineAmount * (vatRate / 100);
        double totalAmount = medicineAmount + vatAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("medicineAmount", medicineAmount);
        response.put("vatRate", vatRate);
        response.put("vatAmount", vatAmount);
        response.put("totalAmount", totalAmount);

        return response;
    }
}