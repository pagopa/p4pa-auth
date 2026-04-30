package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.BrokerClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = it.gov.pagopa.payhub.auth.config.CacheConfig.Fields.broker)
public class BrokerServiceImpl implements BrokerService {

    private final BrokerClient entityClient;

    public BrokerServiceImpl(BrokerClient entityClient) {
        this.entityClient = entityClient;
    }

    @Override
    @Cacheable(key = "#id", unless="#result == null")
    public Broker getBrokerById(Long id, String accessToken) {
        return entityClient.getBrokerById(id, accessToken);
    }
}
