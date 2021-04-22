package lv.dev.mintos.weatherdemo.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class IpService {

    private static final String DEBUG_IP_HEADER = "Debug-IP";

    public String userIp(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(DEBUG_IP_HEADER))
                .orElse(request.getRemoteAddr());
    }

}
