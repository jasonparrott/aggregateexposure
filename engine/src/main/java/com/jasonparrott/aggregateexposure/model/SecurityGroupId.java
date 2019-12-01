package com.jasonparrott.aggregateexposure.model;

import java.time.LocalDate;
import java.util.Objects;

public class SecurityGroupId {
    private final int securityId;
    private final ProductType productType;
    private final LocalDate today;
    private final LocalDate previous;

    public SecurityGroupId(int securityId, ProductType productType, LocalDate today, LocalDate previous) {
        this.securityId = securityId;
        this.productType = productType;
        this.today = today;
        this.previous = previous;
    }

    public int getSecurityId() {
        return securityId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public LocalDate getToday() {
        return today;
    }

    public LocalDate getPrevious() {
        return previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityGroupId that = (SecurityGroupId) o;
        return securityId == that.securityId &&
                productType == that.productType &&
                Objects.equals(today, that.today) &&
                Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(securityId, productType, today, previous);
    }

    @Override
    public String toString() {
        return "SecurityGroupId{" +
                "securityId=" + securityId +
                ", productType=" + productType +
                ", today=" + today +
                ", previous=" + previous +
                '}';
    }
}
