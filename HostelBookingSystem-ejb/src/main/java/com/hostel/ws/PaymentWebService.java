package com.hostel.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.math.BigDecimal;

@WebService(name = "PaymentWebService", targetNamespace = "http://ws.hostel.com/")
public interface PaymentWebService {

    @WebMethod(operationName = "verifyPayment")
    @WebResult(name = "verified")
    boolean verifyPayment(
        @WebParam(name = "transactionId") String transactionId,
        @WebParam(name = "amount") BigDecimal amount
    );

    @WebMethod(operationName = "getTransactionStatus")
    @WebResult(name = "status")
    String getTransactionStatus(
        @WebParam(name = "transactionId") String transactionId
    );
}
