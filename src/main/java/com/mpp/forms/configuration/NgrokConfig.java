package com.mpp.forms.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class NgrokConfig {

    private static final Logger log = LoggerFactory.getLogger(NgrokConfig.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port:8080}")
    private int serverPort;

    private String ngrokUrl;

//    @Profile("dev")
//    @EventListener(ApplicationReadyEvent.class)
//    public void startNgrokProcess() {
//        try {
//            Process process = new ProcessBuilder("ngrok", "http", String.valueOf(serverPort))
//                    .redirectErrorStream(true)
//                    .start();
//
//            this.ngrokUrl = retrieveNgrokUrl();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    // TODO: Initiate ngrok with Dockerfile and start it in this class
    public String getNgrokUrl() {
        if (this.ngrokUrl == null || this.ngrokUrl.isEmpty()) {
            this.ngrokUrl = retrieveNgrokUrl();
        }
        return this.ngrokUrl;
    }

    private String retrieveNgrokUrl() {
        int attempts = 3;
        int count = 0;
        String retrievedNgrokUrl = null;

        while (count < attempts) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                HttpEntity httpEntity = new HttpEntity(headers);

                ResponseEntity<Map> ngrokTunnelResponse = restTemplate.exchange("http://forms-application-ngrok:4040/api/tunnels", HttpMethod.GET, httpEntity, Map.class);
//                ResponseEntity<Map> ngrokTunnelResponse = restTemplate.exchange("http://localhost:4040/api/tunnels", HttpMethod.GET, httpEntity, Map.class);
                Map<String, Object> mapOfNgrokTunnelResponse = ngrokTunnelResponse.getBody();
                List<Map<String, Object>> mapOfTunnels = (List<Map<String, Object>>) mapOfNgrokTunnelResponse.get("tunnels");
                retrievedNgrokUrl = mapOfTunnels.get(0).get("public_url").toString();

                log.info("===================================");
                log.info("  ngrok public URL: {}", retrievedNgrokUrl);
                log.info("===================================");

                return retrievedNgrokUrl;
            } catch (Exception e) {
                log.error("Error trying to retrieve NGROK URL: {}", e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
            count++;
        }

        if (retrievedNgrokUrl == null) {
            throw new RuntimeException("Nao foi possivel buscar o link do NGROK, encerrando aplicacao!");
        }

        return retrievedNgrokUrl;
    }
}