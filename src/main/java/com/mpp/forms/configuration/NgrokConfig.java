package com.mpp.forms.configuration;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
public class NgrokConfig {

    private static final Logger log = LoggerFactory.getLogger(NgrokConfig.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port:8080}")
    private int serverPort;

    @Getter
    private String ngrokUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void startNgrokProcess() {
        try {
            Process process = new ProcessBuilder("ngrok", "http", String.valueOf(serverPort))
                    .redirectErrorStream(true)
                    .start();

//        Preciso para o NGROK rodar corretamente
            Thread.sleep(2000);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity httpEntity = new HttpEntity(headers);

            ResponseEntity<Map> ngrokTunnelResponse = restTemplate.exchange("http://localhost:4040/api/tunnels", HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> mapOfNgrokTunnelResponse = ngrokTunnelResponse.getBody();
            List<Map<String, Object>> mapOfTunnels = (List<Map<String, Object>>) mapOfNgrokTunnelResponse.get("tunnels");
            ngrokUrl = mapOfTunnels.get(0).get("public_url").toString();

            log.info("===================================");
            log.info("  ngrok public URL: {}", ngrokUrl);
            log.info("===================================");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Initiate ngrok with Dockerfile and start it in this class
}