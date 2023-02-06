package com.server.cogito.report.controller;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.report.dto.ReportRequest;
import com.server.cogito.report.dto.ReportResponse;
import com.server.cogito.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/{postId}/posts")
    public ReportResponse reportPost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId,
                                     @RequestBody ReportRequest reportRequest){
        return reportService.reportPost(authUser, postId, reportRequest);
    }

    @PostMapping("/{commentId}/comments")
    public ReportResponse reportComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId,
                                        @RequestBody ReportRequest reportRequest){
        return reportService.reportComment(authUser, commentId, reportRequest);
    }
}
