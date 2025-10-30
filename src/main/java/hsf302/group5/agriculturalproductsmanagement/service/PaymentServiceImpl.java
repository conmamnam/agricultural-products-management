package hsf302.group5.agriculturalproductsmanagement.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    // TODO: Thay thế bằng cấu hình thực tế từ VNPay
    private static final String VNP_TMN_CODE = "YOUR_VNP_TMN_CODE";
    private static final String VNP_HASH_SECRET = "YOUR_VNP_HASH_SECRET";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL = "http://localhost:8080/payment/vnpayReturn"; // Cần khớp với controller

    @Override
    public String createVnPayPayment(int orderId, long amount, String orderInfo, HttpServletRequest request) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(orderId) + "_" + System.currentTimeMillis(); // Mã giao dịch duy nhất
        String vnp_IpAddr = "127.0.0.1"; // TODO: Lấy IP động từ request
        String vnp_TmnCode = VNP_TMN_CODE;
        String vnp_Amount = String.valueOf(amount * 100); // VNPay yêu cầu nhân 100

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // Build query
                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        return VNP_URL + "?" + queryUrl;
    }

    @Override
    public Map<String, String> handleVnPayCallback(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            result.put("status", "success");
            result.put("message", "Thanh toán thành công");
            result.put("orderId", params.get("vnp_TxnRef")); // Lấy mã đơn hàng
            result.put("bankCode", params.get("vnp_BankCode"));
            result.put("amount", params.get("vnp_Amount"));
        } else {
            result.put("status", "fail");
            result.put("message", "Thanh toán thất bại");
        }
        return result;
    }
}