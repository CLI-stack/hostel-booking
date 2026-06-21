package com.hostel.ws;

import jakarta.ejb.Stateless;
import jakarta.jws.WebService;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Stateless session bean published as a JAX-WS Web Service.
 * Simulates an external Payment Verification Service.
 * In production, this would delegate to a real payment gateway (e.g., FPX, Stripe).
 */
@Stateless
@WebService(
    serviceName  = "PaymentVerificationService",
    portName     = "PaymentWebServicePort",
    name         = "PaymentWebService",
    endpointInterface = "com.hostel.ws.PaymentWebService",
    targetNamespace   = "http://ws.hostel.com/"
)
public class PaymentWebServiceImpl implements PaymentWebService {

    private static final Logger LOG = Logger.getLogger(PaymentWebServiceImpl.class.getName());

    @Override
    public boolean verifyPayment(String transactionId, BigDecimal amount) {
        LOG.info("Verifying payment: transactionId=" + transactionId + ", amount=" + amount);

        if (transactionId == null || transactionId.isBlank()) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // Simulated verification logic:
        // A real implementation would call the payment gateway API over HTTPS.
        // Encrypted transmission requirement is met via SSL/TLS on the WS endpoint.
        // Transaction IDs starting with "FAIL" simulate failures for testing.
        if (transactionId.startsWith("FAIL")) {
            return false;
        }

        return true;
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return "INVALID";
        }
        if (transactionId.startsWith("FAIL")) {
            return "FAILED";
        }
        return "VERIFIED";
    }
}
