package com.jasonparrott.aggregateexposure.partitioner;

import com.jasonparrott.aggregateexposure.BusinessDateService;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import com.jasonparrott.aggregateexposure.model.position.Position;

import java.time.LocalDate;
import java.util.Objects;

public class ClientIdPartitioner implements Partitioner {

    private final BusinessDateService businessDateService;

    public ClientIdPartitioner(BusinessDateService businessDateService) {
        this.businessDateService = businessDateService;
    }

    @Override
    public SecurityGroupId findGroupForPosition(Position position) {
        return new ClientIdSecurityGroupId(position.getUnderlyingTrade().getSecurityId(),
                position.getUnderlyingTrade().getProductType(),
                businessDateService.getToday(),
                businessDateService.getPrevious(),
                position.getUnderlyingTrade().getClient().getId());
    }

    public class ClientIdSecurityGroupId extends SecurityGroupId {
        private final int clientId;

        public ClientIdSecurityGroupId(int securityId, ProductType productType, LocalDate today, LocalDate previous, int clientId) {
            super(securityId, productType, today, previous);
            this.clientId = clientId;
        }

        public int getClientId() {
            return clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            ClientIdSecurityGroupId that = (ClientIdSecurityGroupId) o;
            return clientId == that.clientId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), clientId);
        }
    }
}
