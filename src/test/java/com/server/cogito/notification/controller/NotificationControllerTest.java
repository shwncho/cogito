package com.server.cogito.notification.controller;

import com.server.cogito.notification.dto.NotificationResponse;
import com.server.cogito.notification.dto.NotificationResponses;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


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

    @Test
    @DisplayName("알림 전체 조회")
    public void get_notifications_success() throws Exception {
        //given
        NotificationResponses responses = createNotificationResponses();
        given(notificationService.getNotifications(any())).willReturn(responses);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/notifications")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationResponses[0].id").value(responses.getNotificationResponses().get(0).getId()))
                .andExpect(jsonPath("$.notificationResponses[0].content").value(responses.getNotificationResponses().get(0).getContent()))
                .andExpect(jsonPath("$.notificationResponses[0].url").value(responses.getNotificationResponses().get(0).getUrl()))
                .andExpect(jsonPath("$.notificationResponses[1].id").value(responses.getNotificationResponses().get(1).getId()))
                .andExpect(jsonPath("$.notificationResponses[1].content").value(responses.getNotificationResponses().get(1).getContent()))
                .andExpect(jsonPath("$.notificationResponses[1].url").value(responses.getNotificationResponses().get(1).getUrl()))
                .andExpect(jsonPath("$.unreadCount").value(responses.getUnreadCount()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        responseFields(
                                fieldWithPath("notificationResponses[].id").type(JsonFieldType.NUMBER).description("sse id"),
                                fieldWithPath("notificationResponses[].content").type(JsonFieldType.STRING).description("sse content"),
                                fieldWithPath("notificationResponses[].url").type(JsonFieldType.STRING).description("url"),
                                fieldWithPath("notificationResponses[].createdAt").type(JsonFieldType.STRING).description("알림 생성일"),
                                fieldWithPath("notificationResponses[].isRead").type(JsonFieldType.BOOLEAN).description("알림 확인 여부"),
                                fieldWithPath("unreadCount").type(JsonFieldType.NUMBER).description("읽지 않은 알림 개수")
                        )
                ));
    }

    private static NotificationResponses createNotificationResponses(){
        return NotificationResponses.of(Arrays.asList(
                NotificationResponse.builder()
                        .id(1L)
                        .content("새로운 댓글이 달렸습니다.")
                        .url("/questions/1")
                        .createdAt(LocalDateTime.now())
                        .build(),
                NotificationResponse.builder()
                        .id(2L)
                        .content("새로운 댓글이 달렸습니다.")
                        .url("/questions/1")
                        .createdAt(LocalDateTime.now())
                        .build()),2);

    }

}