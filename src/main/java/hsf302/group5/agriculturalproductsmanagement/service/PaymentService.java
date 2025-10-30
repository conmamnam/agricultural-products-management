package hsf302.group5.agriculturalproductsmanagement.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {

    String createVnPayPayment(int orderId, long amount, String orderInfo, HttpServletRequest request);
    Map<String, String> handleVnPayCallback(Map<String, String> params);
}