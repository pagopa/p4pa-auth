package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.PublicKey;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = A2AClientLegacyPropConfig.class)
@TestPropertySource(properties = {
	"m2m.legacy.public-keys.IPA_TEST_1=MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAuximAPn5uRE55OHqGB/wQneUlQSNQEe6VJMzRCr1Mml5J0Zo7y857DyB+teY38vN1lWMwKyt77yOsrMQyRX3TlD2gSxEXLEhJmqWEmcLm2TXMIhGv/TboL40gsEcI5flubFYqUThbk5fJR/vYkocj7E7Jw0JAKZnav3aS2Gx4cmIlaAarXwHjpM0T+0c8RA3QQB2eZ38gjA1MA6wpqg/avUcgn6+xpv3NNj7KqV6jxDpqKN42vucBtNAtsaP0q3O2s0PbkrOUY+ogOGUxuFfgIbq0nNizZypsN9w/B6coFr5rLkwZgsCTCL/AlAqX6r+/017XHRX8fUiRXQsIux7Xp1u45Rr0ZH1RWDMcoRNJQcHz410PnQqXb7TQCHNV5G6sqmSVKJOj8CLHkqYW0R5VhjvQDzdK62nmSp7lSXQiQOwNpIPK4ijLeU8SnEhCi3+o1Khtq9X6z3XxRh3XbP2n67mJ7EGgVz2Z/zLMuph4k/Cg7o62cCYfCpsqVE96gq10xj+UUfg5ONRoyOZEYYflVpRlrUjhy+wcB4z9YWcjm3gQ5FJXBthTWq47aQqjzhs6uKmJEcwDrJf+lXhQyv+j5lZ4ybPC+s7dknUnHUAw8fXdY5cwYxHdnhJ7dlf6IQJOCcBR1xgmqGUj+iwHmvNnk8jPa3NNaMUc8ruPfU8AX0CAwEAAQ==",
	"m2m.legacy.public-keys.IPA_TEST_2=MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAj/PKsMVKq8SWIhnDl0/pJT9BS5JfXesTllDTunDMjh5wkj97LR4wbGUXJjkf4auZzNJU/NN7muER2qlJjiTA3+OpfqndnVFmHLVG5hwQf6Pjy9fYv+FLTbR8l/O6giKfoGpMmLOVd+mS/QA7aBZMWoSyb9p0pxDg4Wb4MHjSQksXDSBdW/zRqVqiHNuILxpYr98WDactpsVhLyAuBRSvnuSMUat5XpNZFQt0qW5ZunTr8ctNIRIYVMaz5nWo3ker6YGWfU1CJxXV2vT2sFi60G4ngy3TrAX7OAy1vCk144pkDkGZc+6zSfZE1EuZvcq1q+Li3PD1TQ8J81n9tZ1X1xJT5xBBeFy1fExa+WyJGZorc5YkwNvq6HdCsTp3vtqszMvqHNz8xXk5VskbSICxKEdqdyV2FdkmbEl16pkm4UJZgiwRYlzs6THJ+wVUzuDaJZVMOcGB9Uv3kEVsXgagfHP6CITNrVJgixUBc92pgNCc11E6HOVdxsklNZ2m/PfBjiqk+sX31CFfTV2kKHdujs7E4e18etqLWVVt+ygG7PYD5r6ZB0tZVT4Vadowh4+R2/aAsrxGFzW5tHm0GDyu0e7ahK0ltdgK5slyLIEWa8cf6F0dX3JFDOtg3Iawllje4nPvOK8h8WDr2TMI1sHy3XEMvCY8eHcg0izjdI3uGJ8CAwEAAQ=="
})
class A2AClientLegacyPropConfigTest {

	@Autowired
	private A2AClientLegacyPropConfig propConfig;

	@Test
	void testGetPublicKeysAsMap() {
		Map<String, PublicKey> publicKeyMap = propConfig.getPublicKeysAsMap();

		Assertions.assertTrue(publicKeyMap.containsKey("IPA_TEST_1"));
		Assertions.assertTrue(publicKeyMap.containsKey("IPA_TEST_2"));
		Assertions.assertEquals(2, publicKeyMap.size());
	}
}
