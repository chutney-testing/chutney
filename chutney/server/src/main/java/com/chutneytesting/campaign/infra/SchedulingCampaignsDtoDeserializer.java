/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;

import com.chutneytesting.campaign.infra.SchedulingCampaignDto.CampaignExecutionRequestDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

class SchedulingCampaignsDtoDeserializer extends StdDeserializer<SchedulingCampaignDto> {

    protected SchedulingCampaignsDtoDeserializer() {
        super(SchedulingCampaignDto.class);
    }

    @Override
    public SchedulingCampaignDto deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.readValueAsTree();

        String id = node.get("id").asText();
        LocalDateTime schedulingDate = getSchedulingDate(node);
        String frequency = getFrequency(node);
        List<Long> campaignsId = getLongList(node.get("campaignsId"));
        List<String> campaignsTitle = getStringList(node.get("campaignsTitle"));
        String environment = node.has("environment") ? node.get("environment").asText() : null;
        List<String> datasetIds = getStringList(node.get("datasetsId"));

        List<CampaignExecutionRequestDto> campaignExecutionRequestDto =
            IntStream.range(0, campaignsId.size())
                .mapToObj(i -> new CampaignExecutionRequestDto(
                    campaignsId.get(i),
                    campaignsTitle.get(i),
                    guardArrayOfBoundException(datasetIds, i))
                )
                .collect(Collectors.toList());


        return new SchedulingCampaignDto(id, schedulingDate, frequency, environment, campaignExecutionRequestDto);
    }

    private String guardArrayOfBoundException(List<String> list, int i) {
        if (i < list.size()) {
            return list.get(i);
        }
        return "";
    }

    private List<Long> getLongList(JsonNode node) {
        return extractList(node, JsonNode::asLong);
    }

    private List<String> getStringList(JsonNode node) {
        return extractList(node, JsonNode::asText);
    }

    private <T> List<T> extractList(JsonNode node, Function<JsonNode, T> mapper) {
        if (node == null) {
            return emptyList();
        }
        return StreamSupport.stream(node.spliterator(), false)
            .map(mapper)
            .collect(toCollection(ArrayList::new));
    }

    private LocalDateTime getSchedulingDate(JsonNode node) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.readValue(node.get("schedulingDate").toString(), LocalDateTime.class);
    }

    private String getFrequency(JsonNode node) {
        return node.has("frequency") ? node.get("frequency").asText() : null;
    }
}
