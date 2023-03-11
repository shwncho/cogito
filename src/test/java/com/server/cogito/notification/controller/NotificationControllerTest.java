package com.server.cogito.notification.controller;

import com.server.cogito.notification.service.NotificationService;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(NotificationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class NotificationControllerTest extends RestDocsSupport {

    @MockBean
    NotificationService notificationService;

    @Test
    @DisplayName("sse 연결 성공")
    public void get_subscribe_success() throws Exception {
        //given
        String id = "1" + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter();
                emitter.send(SseEmitter.event()
                .id(id)
                .name("sse")
                .data("EventStream Created. [userId=" + "1" + "]")
                .build());
        given(notificationService.subscribe(any(),any())).willReturn(emitter);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/notifications/subscribe")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(header().string("X-Accel-Buffering","no"))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("lastEventId").optional().description("sse 재연결 할 경우 마지막으로 전달받은 id 값")
                        )
                ));

    }

}