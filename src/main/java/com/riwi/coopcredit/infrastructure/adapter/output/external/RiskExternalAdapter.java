package com.riwi.coopcredit.infrastructure.adapter.output.external;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.domain.port.out.RiskExternalPort;
import com.riwi.coopcredit.infrastructure.adapter.output.external.dto.RiskRequestDto;
import com.riwi.coopcredit.infrastructure.adapter.output.external.dto.RiskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RiskExternalAdapter implements RiskExternalPort {

    private final WebClient riskWebClient;

    @Override
    public Integer getRiskScore(String document, Double amount, Integer term) {
        RiskRequestDto request = RiskRequestDto.builder()
                .documento(document)
                .monto(amount)
                .plazo(term)
                .build();

        RiskResponseDto response = riskWebClient.post()
                .uri("/risk-evaluation")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new DomainException("Error de servicio externo de riesgo. Código: " + clientResponse.statusCode()))
                )
                .bodyToMono(RiskResponseDto.class)
                .block(Duration.ofSeconds(5)); // Bloqueamos el flujo de forma controlada

        if (response == null || response.getScore() == null) {
            throw new DomainException("El servicio de riesgo no retornó una evaluación válida o fue nulo.");
        }

        return response.getScore();
    }
}