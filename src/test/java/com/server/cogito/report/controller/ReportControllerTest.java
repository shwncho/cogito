package com.server.cogito.report.controller;

import com.server.cogito.report.dto.ReportRequest;
import com.server.cogito.report.dto.ReportResponse;
import com.server.cogito.report.service.ReportService;
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

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ReportController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class ReportControllerTest extends RestDocsSupport {

    @MockBean
    private ReportService reportService;

    @Test
    @DisplayName("게시물 신고 성공")
    public void report_post_success() throws Exception {
        //given
        ReportRequest request = new ReportRequest("게시물 신고 테스트");
        ReportResponse response = new ReportResponse("신고 누적 횟수: "+1);
        given(reportService.reportPost(any(),any(),any()))
                .willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/reports/1/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is("신고 누적 횟수: "+1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestFields(
                                fieldWithPath("reason").type(JsonFieldType.STRING).description("신고 사유")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("신고 누적 횟수")
                        )));
    }
}