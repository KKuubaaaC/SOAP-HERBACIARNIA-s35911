package com.example.herbaciarnia.endpoint;

import com.example.herbaciarnia.config.SoapWSConfig;
import com.example.herbaciarnia.model.Tea;
import com.example.herbaciarnia.repo.TeaRepository;
import com.example.herbaciarnia.teas.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Endpoint
@RequiredArgsConstructor
public class TeaEndpoint {

    private final TeaRepository teaRepository;

    @PayloadRoot(namespace = SoapWSConfig.TEA_NAMESPACE, localPart = "getTeasRequest")
    @ResponsePayload
    public GetTeasResponse getTeas(@RequestPayload GetTeasRequest req) {
        List<Tea> all = teaRepository.findAll();
        GetTeasResponse response = new GetTeasResponse();
        all.stream()
                .map(this::toDto)
                .forEach(dto -> response.getTeas().add(dto));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.TEA_NAMESPACE, localPart = "getTeaByIdRequest")
    @ResponsePayload
    public GetTeaByIdResponse getTeaById(@RequestPayload GetTeaByIdRequest req) {
        Long id = req.getTeaId().longValue();
        Optional<Tea> found = teaRepository.findById(id);
        GetTeaByIdResponse response = new GetTeaByIdResponse();
        found.ifPresent(tea -> response.setTea(toDto(tea)));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.TEA_NAMESPACE, localPart = "addTeaRequest")
    @ResponsePayload
    public AddTeaResponse addTea(@RequestPayload AddTeaRequest req) {
        Tea tea = toEntity(req.getTea());
        Long id = teaRepository.save(tea).getId();
        AddTeaResponse response = new AddTeaResponse();
        response.setTeaId(new BigDecimal(id));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.TEA_NAMESPACE, localPart = "brewTeaRequest")
    @ResponsePayload
    public BrewTeaResponse brewTea(@RequestPayload BrewTeaRequest req) {
        Long id = req.getTeaId().longValue();
        BrewTeaResponse response = new BrewTeaResponse();

        teaRepository.findById(id).ifPresent(tea -> {
            BrewingParametersDto params = new BrewingParametersDto();
            params.setTeaName(tea.getName());
            params.setTeaType(TeaTypeEnum.fromValue(tea.getType()));
            if (tea.getTemperatureCelsius() != null) {
                params.setTemperatureCelsius(BigInteger.valueOf(tea.getTemperatureCelsius()));
            }
            if (tea.getSteepingTimeSeconds() != null) {
                params.setSteepingTimeSeconds(BigInteger.valueOf(tea.getSteepingTimeSeconds()));
            }
            params.setWaterAmountMl(BigInteger.valueOf(calculateWaterAmount(tea.getType())));
            params.setTeaAmountGrams(calculateTeaAmount(tea.getType()));
            params.setBrewingAdvice(generateAdvice(tea));
            response.setBrewingParameters(params);
        });

        return response;
    }

    private int calculateWaterAmount(String type) {
        return switch (type) {
            case "GREEN", "WHITE" -> 150;
            case "OOLONG" -> 120;
            case "PU_ERH" -> 100;
            default -> 200;
        };
    }

    private BigDecimal calculateTeaAmount(String type) {
        return switch (type) {
            case "GREEN", "WHITE" -> new BigDecimal("2.0");
            case "OOLONG" -> new BigDecimal("4.0");
            case "PU_ERH" -> new BigDecimal("5.0");
            default -> new BigDecimal("3.0");
        };
    }

    private String generateAdvice(Tea tea) {
        return switch (tea.getType()) {
            case "GREEN" -> "Używaj wody niechlorowanej. Nie przekraczaj " + tea.getTemperatureCelsius() + "°C.";
            case "BLACK" -> "Można dodać mleko po zaparzeniu. Temperatura " + tea.getTemperatureCelsius() + "°C.";
            case "WHITE" -> "Delikatna herbata — parz krótko, " + tea.getSteepingTimeSeconds() + "s.";
            case "OOLONG" -> "Można parzyć wielokrotnie, każde kolejne parzenie 30s dłużej.";
            case "HERBAL" -> "Można słodzić miodem. Bez kofeiny.";
            case "PU_ERH" -> "Wypłucz liście wrzątkiem przez 10s przed właściwym parzeniem.";
            default -> "Parzyć zgodnie z instrukcją producenta.";
        };
    }

    private TeaDto toDto(Tea t) {
        TeaDto dto = new TeaDto();
        if (t.getId() != null) {
            dto.setId(new BigDecimal(t.getId()));
        }
        dto.setName(t.getName());
        dto.setOrigin(t.getOrigin());
        dto.setType(TeaTypeEnum.fromValue(t.getType()));
        if (t.getTemperatureCelsius() != null) {
            dto.setTemperatureCelsius(t.getTemperatureCelsius());
        }
        if (t.getSteepingTimeSeconds() != null) {
            dto.setSteepingTimeSeconds(t.getSteepingTimeSeconds());
        }
        if (t.getNotes() != null) {
            dto.setNotes(t.getNotes());
        }
        return dto;
    }

    private Tea toEntity(TeaDto dto) {
        return Tea.builder()
                .name(dto.getName())
                .origin(dto.getOrigin())
                .type(dto.getType().value())
                .temperatureCelsius(dto.getTemperatureCelsius())
                .steepingTimeSeconds(dto.getSteepingTimeSeconds())
                .notes(dto.getNotes())
                .build();
    }
}
