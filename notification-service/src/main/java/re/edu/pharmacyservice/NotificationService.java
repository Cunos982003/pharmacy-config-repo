package re.edu.pharmacyservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import re.edu.pharmacyservice.KafkaProperties;

@Service
@RefreshScope
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${spring.kafka.template.default-topic:medicine-stock-events}")
    private String topicName;

    @KafkaListener(
        topics = "${spring.kafka.template.default-topic:medicine-stock-events}",
        groupId = "notification-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderEvent orderEvent) {
        logger.info("Hóa đơn cho đơn hàng {} đã được gửi tới khách hàng", orderEvent.getOrderId());

        // In a real implementation, here you would send an email with the order details
        // This is a placeholder for the actual email sending logic
        // Email content could include:
        // - Order ID
        // - Medicine details
        // - Quantity
        // - Total price
        // - Customer information
        //
        // Example email content generation:
        // String emailBody = generateEmailContent(orderEvent);
        // emailService.sendEmail(customerEmail, \"Your Order Confirmation\", emailBody);

        // For now, we're just logging the notification as required
        logger.info("Email notification sent for order: {}", orderEvent.getOrderId());
    }
}