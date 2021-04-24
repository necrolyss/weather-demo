package lv.dev.mintos.weatherdemo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class IpServiceTest {

    public static final String DEBUG_IP = "123.123.123.123";
    public static final String NON_DEBUG_IP = "185.80.236.209";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private IpService ipService;

    @Test
    void resolveIpFromHeader() {
        given(request.getHeader("Debug-IP")).willReturn(DEBUG_IP);

        assertThat(ipService.userIp(request)).isEqualTo(DEBUG_IP);
    }

    @Test
    void resolvesIpFromRequest() {
        given(request.getRemoteAddr()).willReturn(NON_DEBUG_IP);

        assertThat(ipService.userIp(request)).isEqualTo(NON_DEBUG_IP);
    }
}