package re.edu.pharmacyservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RefreshScope
public class KafkaInventoryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInventoryConsumer.class);

    private final MedicineRepository medicineRepository;

    @Value("${spring.kafka.template.default-topic:medicine-stock-events}")
    private String topicName;

    public KafkaInventoryConsumer(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @KafkaListener(
        topics = "${spring.kafka.template.default-topic:medicine-stock-events}",
        groupId = "inventory-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderEvent orderEvent) {
        logger.info("Received order event for medicineId: {}, quantity: {}", orderEvent.getMedicineId(), orderEvent.getQuantity());

        try {
            // Find the medicine by its ID
            Optional<Medicine> medicineOptional = medicineRepository.findById(Long.parseLong(orderEvent.getMedicineId()));

            if (medicineOptional.isPresent()) {
                Medicine medicine = medicineOptional.get();

                // Check if there is enough stock
                if (medicine.getQuantity() >= orderEvent.getQuantity()) {
                    // Update the quantity by subtracting the ordered amount
                    medicine.setQuantity(medicine.getQuantity() - orderEvent.getQuantity());
                    medicine.setUpdatedAt(java.time.LocalDateTime.now());

                    medicineRepository.save(medicine);

                    logger.info("Updated inventory for medicineId: {}. New quantity: {}",
                        orderEvent.getMedicineId(), medicine.getQuantity());
                } else {
                    logger.error("Insufficient stock for medicineId: {}. Available: {}, Requested: {}",
                        orderEvent.getMedicineId(), medicine.getQuantity(), orderEvent.getQuantity());
                    throw new RuntimeException("Insufficient stock for medicineId: " + orderEvent.getMedicineId());
                }
            } else {
                logger.error("Medicine not found with ID: {}", orderEvent.getMedicineId());
                throw new RuntimeException("Medicine not found with ID: " + orderEvent.getMedicineId());
            }

        } catch (NumberFormatException e) {
            logger.error("Invalid medicineId format: {}", orderEvent.getMedicineId(), e);
            throw new RuntimeException("Invalid medicineId format: " + orderEvent.getMedicineId(), e);
        } catch (Exception e) {
            logger.error("Error processing order event for medicineId: {}", orderEvent.getMedicineId(), e);
            throw new RuntimeException("Error processing order event for medicineId: " + orderEvent.getMedicineId(), e);
        }
    }
}