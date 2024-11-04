package io.zenwave360.example.core.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

/** Enum for PaymentMethodType. */
public enum PaymentMethodType {

    VISA(1), MASTERCARD(2),;

    private final Integer value;

    private PaymentMethodType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static PaymentMethodType fromValue(Integer value) {
        return Arrays.stream(PaymentMethodType.values()).filter(e -> e.value.equals(value)).findFirst().orElse(null);
    }

    @Converter
    static class PaymentMethodTypeConverter implements AttributeConverter<PaymentMethodType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(PaymentMethodType attribute) {
            if (attribute == null) {
                return null;
            }

            return attribute.value;
        }

        @Override
        public PaymentMethodType convertToEntityAttribute(Integer dbData) {
            return PaymentMethodType.fromValue(dbData);
        }

    }

}
