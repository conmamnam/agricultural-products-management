package hsf302.group5.agriculturalproductsmanagement.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String createVnPayPayment(int orderId, long amount, String orderInfo, HttpServletRequest request) {

        String vnp_TxnRef = String.valueOf(orderId) + "_" + System.currentTimeMillis();

        String responseCode = "00";
        String bankCode = "NCB";
        String amountStr = String.valueOf(amount * 100);
        String orderInfoEncoded;
        try {
            orderInfoEncoded = URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            orderInfoEncoded = "Thanh toan";
        }

        String redirectUrl = "/payment/vnpayReturn" +
                "?vnp_Amount=" + amountStr +
                "&vnp_BankCode=" + bankCode +
                "&vnp_ResponseCode=" + responseCode +
                "&vnp_OrderInfo=" + orderInfoEncoded +
                "&vnp_TxnRef=" + vnp_TxnRef;

        return "redirect:" + redirectUrl;
    }

    @Override
    public Map<String, String> handleVnPayCallback(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            result.put("status", "success");
            result.put("message", "Thanh toán thành công (Giả lập)");
            result.put("orderId", params.get("vnp_TxnRef"));
            result.put("bankCode", params.get("vnp_BankCode"));
            result.put("amount", params.get("vnp_Amount"));
        } else {
            result.put("status", "fail");
            result.put("message", "Thanh toán thất bại (Giả lập)");
        }
        return result;
    }
}