package com.mingle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

/** PortOne V2 결제 결과를 서버에서 검증한다. */
@Service
@Log4j
public class PortOnePaymentService {

    private static final String DEFAULT_API_URL = "https://api.portone.io";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${portone.api.secret:}")
    private String configuredApiSecret;

    @Value("${portone.store.id:}")
    private String configuredStoreId;

    public VerifiedPayment verify(String paymentId, String merchantUid) {

        if (isBlank(paymentId) || isBlank(merchantUid)) {
            throw new IllegalArgumentException("결제 인증 정보가 없습니다.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne "
                    + requiredSetting(configuredApiSecret, "PORTONE_IMP_SECRET", "portone.api.secret"));

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl() + "/payments/" + paymentId,
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    String.class);

            JsonNode payment = objectMapper.readTree(response.getBody());
            String verifiedPaymentId = payment.path("id").asText();
            String verifiedStoreId = payment.path("storeId").asText();
            String status = payment.path("status").asText();

            if (!paymentId.equals(verifiedPaymentId)
                    || !paymentId.equals(merchantUid)
                    || !configuredStoreId.equals(verifiedStoreId)
                    || !"PAID".equalsIgnoreCase(status)) {
                throw new IllegalArgumentException("정상 결제로 확인되지 않았습니다.");
            }

            return new VerifiedPayment(
                    paymentId,
                    paymentId,
                    payment.path("amount").path("total").asInt());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("PortOne V2 결제 검증 실패", e);
            throw new IllegalStateException("결제 검증 서버와 통신하지 못했습니다.");
        }
    }

    private String apiUrl() {
        String configured = System.getProperty("portone.api-url");
        return isBlank(configured) ? DEFAULT_API_URL : configured;
    }

    private String requiredSetting(String configuredValue, String environmentName, String propertyName) {
        String value = configuredValue;
        if (isBlank(value)) {
            value = System.getProperty(propertyName);
        }
        if (isBlank(value)) {
            value = System.getProperty(environmentName);
        }
        if (isBlank(value)) {
            value = System.getenv(environmentName);
        }
        if (isBlank(value)) {
            throw new IllegalStateException(propertyName + " 설정이 없습니다.");
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class VerifiedPayment {

        private final String impUid;
        private final String merchantUid;
        private final int amount;

        public VerifiedPayment(String impUid, String merchantUid, int amount) {
            this.impUid = impUid;
            this.merchantUid = merchantUid;
            this.amount = amount;
        }

        public String getImpUid() {
            return impUid;
        }

        public String getMerchantUid() {
            return merchantUid;
        }

        public int getAmount() {
            return amount;
        }
    }
}
