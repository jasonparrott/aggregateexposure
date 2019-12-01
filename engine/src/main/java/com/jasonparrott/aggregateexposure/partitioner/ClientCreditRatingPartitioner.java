package com.jasonparrott.aggregateexposure.partitioner;

import com.jasonparrott.aggregateexposure.BusinessDateService;
import com.jasonparrott.aggregateexposure.model.CreditRating;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import com.jasonparrott.aggregateexposure.model.position.Position;

import java.time.LocalDate;
import java.util.Objects;

public class ClientCreditRatingPartitioner implements Partitioner {
    private final BusinessDateService businessDateService;

    public ClientCreditRatingPartitioner(BusinessDateService businessDateService) {
        this.businessDateService = businessDateService;
    }

    @Override
    public SecurityGroupId findGroupForPosition(Position position) {
        return new ClientCreditRatingSecurityGroupId(position.getUnderlyingTrade().getSecurityId(),
                position.getUnderlyingTrade().getProductType(),
                businessDateService.getToday(),
                businessDateService.getPrevious(),
                position.getUnderlyingTrade().getClient().getCreditRating());
    }

    public class ClientCreditRatingSecurityGroupId extends SecurityGroupId {
        private final CreditRating creditRating;

        public ClientCreditRatingSecurityGroupId(int securityId, ProductType productType, LocalDate today, LocalDate previous, CreditRating creditRating) {
            super(securityId, productType, today, previous);
            this.creditRating = creditRating;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            ClientCreditRatingSecurityGroupId that = (ClientCreditRatingSecurityGroupId) o;
            return creditRating == that.creditRating;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), creditRating);
        }
    }
}
