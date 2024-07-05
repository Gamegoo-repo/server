package com.gamegoo.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private Long reporterId;

    private Long targetId;

    private Long reportTypeId;

    private String reportContent;
}
