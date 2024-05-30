package it.gov.pagopa.payhub.auth.service.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccessTokenBuilderServiceTest {

    private AccessTokenBuilderService accessTokenBuilderService;

    private final static String PRIVATE_KEY= """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEogIBAAKCAQEA2ovm/rd3g69dq9PisinQ6mWy8ZttT8D+GKXCsHZycsGnN7b7
            4TPyYy+4+h+9cgJeizp8RDRrufHjiBrqi/2reOk/rD7ZHbpfQvHK8MYfgIVdtTxY
            MX/GGdOrX6/5TV2b8e2aCG6GmxF0UuEvxY9oTmcZUxnIeDtl/ixz4DQ754eS363q
            WfEA92opW+jcYzr07sbQtR86e+Z/s/CUeX6W1PHNvBqdlAgp2ecr/1DOLq1D9hEA
            NBPSwbt+FM6FNe4vLphi7GTwiB0yaAuy+jE8odND6HPvvvmgbK1/2qTHn/HJjWUm
            11LUC73BszR32BKbdEEhxPQnnwswVekWzPi1IwIDAQABAoIBAFH4aWKeY8hTjTm2
            lm+muYJBNNXkKyLfyy5pddWEB7c9JUADdQPp3P8Q1juSjhbmBpoIDLX0R3eN336c
            Qd7R/W+zZLtxMzQwRCyyziBy3zvwSc6BXL7sItxrBPs14LcA5k3ehYimE/yzlkLD
            zYw3FrNZfikqIYPfG4kzGR892D4lbSMTTzXgBtPEyM3TmBTDwbJ5hk6xMx9AwGec
            mwz8izWNFhgc3LxrI4KnMZz6dikhScCThTHL58ZdgSdMHPjTK+Xhh7pnwFF5D3bt
            H5jVsuqDX107mo/TeKyovt2H6xQMleVfjWUW6JJUxEoRtPSYBg0ogtbK337k8AoG
            BqldO6ECgYEA6oXwrG+lAEe/wLwgLqT2kpjPiK8JHw0MzfzljHI0H9Sh7/ga5yrK
            WZyMEm2JlhyGSXdMzm4CCeNsP4fJVVo1uZiYGZkl0wOlPSsd6DkXfLfC4qiyU2qm
            Vk0BgiQRKwoWrFv2mPl9/AJWRSbrCk4223K2yWzieYgwyfY4nJ5h8BECgYEA7o9p
            QYbzNhrDMyObawNz4UwR3zhMzmmnopGHn8XO9OmARDDM1CGci37TJUYK9tajjFIz
            H0YwLcL/6/cu8kxs35hXBdmZgYv+jmT8zekMNp37kqjm4u+Ac8UnleCUqufdEpIY
            Uvnak08C9XZtPkYvVJlQ8KyotXRsXvLYo5i5hfMCgYA/NPAjmUdwJuZATLOjvqQR
            6ItufDZKHxtHXRSE4La5qXYnlceya+7zbeS2hr0hLvjmTffuXum/voKLMM6LaW+3
            YLAFnif6ki3zqW47C0AQRfqJWgwNvV2tPr3cVFooLmTj+TkiC4Pv6rVTl+Sa92+D
            f4xSBz2WoaT8mZayZ2Ff8QKBgDHtacYBDF3CdB/7z8cxzcrVNNhW3BxHGIJ5mrzh
            lVLEm8epvvSWpEC9pksiwaCvg0MW4QQmmGa7bPxhmz2yqQaSx4O96tamCfybPh2K
            LLgxkDk9iDTukx+nn4VKn1K1fBsq4FRdXlV+L8xXoL1ryvQVsk7sk9KGLzgf8x8q
            E4npAoGABrR0F9hWlqGaENoXujNEN7LTzsVWXRD490dn0oawVYPzpuX0l2vCjNp/
            veDYA00ElNOLz9WdkniU7BuTf9+9oWZCDXbJvK7HvidxVv0owbx02CxDL+UV98kr
            XtIA8ZH/XPiyad0JjP4wDTgygKzmYdXgmVjO9/NcOA2jpvyugdw=
            -----END RSA PRIVATE KEY-----
            """;

    private final static String PUBLIC_KEY= """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2ovm/rd3g69dq9PisinQ
            6mWy8ZttT8D+GKXCsHZycsGnN7b74TPyYy+4+h+9cgJeizp8RDRrufHjiBrqi/2r
            eOk/rD7ZHbpfQvHK8MYfgIVdtTxYMX/GGdOrX6/5TV2b8e2aCG6GmxF0UuEvxY9o
            TmcZUxnIeDtl/ixz4DQ754eS363qWfEA92opW+jcYzr07sbQtR86e+Z/s/CUeX6W
            1PHNvBqdlAgp2ecr/1DOLq1D9hEANBPSwbt+FM6FNe4vLphi7GTwiB0yaAuy+jE8
            odND6HPvvvmgbK1/2qTHn/HJjWUm11LUC73BszR32BKbdEEhxPQnnwswVekWzPi1
            IwIDAQAB
            -----END PUBLIC KEY-----
            """;

    @BeforeEach
    void init(){
        accessTokenBuilderService = new AccessTokenBuilderService("APPLICATION_AUDIENCE", 3600,PRIVATE_KEY, PUBLIC_KEY);
    }

    @Test
    void test(){
        // TODO assertions
        System.out.println(accessTokenBuilderService.build());
    }
}
