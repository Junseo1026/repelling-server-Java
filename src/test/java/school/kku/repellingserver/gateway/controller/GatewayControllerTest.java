package school.kku.repellingserver.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import school.kku.repellingserver.common.BaseControllerTest;
import school.kku.repellingserver.gateway.dto.RepellentDataRequest;
import school.kku.repellingserver.gateway.service.GatewayService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GatewayControllerTest extends BaseControllerTest {

    @MockBean
    GatewayService gatewayService;

    @Test
    void 게이트웨이_serialId_가_존재하면_true를_리턴한다() throws Exception {
        //given
        final String serialId = "serialId";

        when(gatewayService.isSerialIdExists(serialId))
                .thenReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/valid/serial-id")
                        .param("serialId", serialId))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("gateway/serial-id/exists/success",
                        queryParameters(
                                parameterWithName("serialId").description("게이트웨이의 serialId")
                        ),
                        responseFields(
                                fieldWithPath("isSerialIdExists").description("게이트웨이의 serialId가 존재하는지 여부")
                        )
                        ));

    }

    @Test
    void 게이트웨이에서_퇴치_데이터를_전달받는다() throws Exception {
        //given
        final RepellentDataRequest request = RepellentDataRequest.of(
                "gatewayId",
                "nodeId",
                "message",
                "soundType",
                "soundLevel",
                LocalDateTime.now()
        );

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/repellent-data")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("repellent-data/success",
                        requestFields(
                                fieldWithPath("gatewayId").description("게이트웨이의 ID"),
                                fieldWithPath("nodeId").description("노드의 ID"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("soundType").description("소리의 종류"),
                                fieldWithPath("soundLevel").description("소리의 크기"),
                                fieldWithPath("timestamp").description("데이터가 전송된 시간")
                        )));
    }

}