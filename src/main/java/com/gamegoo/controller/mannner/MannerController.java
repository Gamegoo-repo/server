package com.gamegoo.controller.mannner;

import com.gamegoo.apiPayload.ApiResponse;
import com.gamegoo.domain.manner.MannerRating;
import com.gamegoo.dto.manner.MannerRequest;
import com.gamegoo.dto.manner.MannerResponse;
import com.gamegoo.service.manner.MannerService;
import com.gamegoo.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/manner")
@Tag(name = "MannerRating", description = "매너 평가 관련 API")
public class MannerController {

    private final MannerService mannerService;

    @PostMapping("/good")
    @Operation(summary = "매너 평가 등록 API", description = "매너 평가하기 API 입니다. 매너 키워드 유형 1~6 을 입력하세요.")
    public ApiResponse<MannerResponse.mannerInsertResponseDTO> mannerInsert(
            @RequestBody @Valid MannerRequest.mannerInsertDTO request) {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerRating mannerrating = mannerService.insertManner(request, memberId);

        List<Long> mannerRatingKeywordList = mannerrating.getMannerRatingKeywordList()
                .stream()
                .map(mannerRatingKeyword -> mannerRatingKeyword.getMannerKeyword().getId())
                .collect(Collectors.toList());

        MannerResponse.mannerInsertResponseDTO result = MannerResponse.mannerInsertResponseDTO.builder()
                .mannerId(mannerrating.getId())
                .toMemberId(mannerrating.getToMember().getId())
                .mannerRatingKeywordList(mannerRatingKeywordList)
                .build();

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/bad")
    @Operation(summary = "비매너 평가 등록 API", description = "비매너 평가하기 API 입니다. 비매너 키워드 유형 7~12 를 입력하세요.")
    public ApiResponse<MannerResponse.mannerInsertResponseDTO> badMannerInsert(
            @RequestBody @Valid MannerRequest.mannerInsertDTO request) {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerRating mannerrating = mannerService.insertBadManner(request, memberId);

        List<Long> mannerRatingKeywordList = mannerrating.getMannerRatingKeywordList()
                .stream()
                .map(mannerRatingKeyword -> mannerRatingKeyword.getMannerKeyword().getId())
                .collect(Collectors.toList());

        MannerResponse.mannerInsertResponseDTO result = MannerResponse.mannerInsertResponseDTO.builder()
                .mannerId(mannerrating.getId())
                .toMemberId(mannerrating.getToMember().getId())
                .mannerRatingKeywordList(mannerRatingKeywordList)
                .build();

        return ApiResponse.onSuccess(result);
    }

    @PutMapping("/{mannerId}")
    @Operation(summary = "매너 평가 수정 API", description = "매너 평가를 수정하는 API 입니다.")
    @Parameter(name = "mannerId", description = "수정할 매너평가 id 입니다.")
    public ApiResponse<MannerResponse.mannerUpdateResponseDTO> mannerUpdate(
            @PathVariable long mannerId,
            @RequestBody MannerRequest.mannerUpdateDTO request) {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerRating updateMannerrating = mannerService.update(request, memberId, mannerId);

        List<Long> mannerRatingKeywords = updateMannerrating.getMannerRatingKeywordList()
                .stream()
                .map(mannerRatingKeyword -> mannerRatingKeyword.getMannerKeyword().getId())
                .collect(Collectors.toList());

        MannerResponse.mannerUpdateResponseDTO result = MannerResponse.mannerUpdateResponseDTO.builder()
                .mannerId(updateMannerrating.getId())
                .mannerRatingKeywordList(mannerRatingKeywords)
                .build();

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/good/{memberId}")
    @Operation(summary = "매너 평가 조회 API", description = "회원이 실시한 매너 평가를 조회하는 API 입니다.")
    @Parameter(name = "memberId", description = "회원이 실시한 매너평가 대상의 id 입니다.")
    public ApiResponse<MannerResponse.mannerKeywordResponseDTO> getMannerKeyword(
            @PathVariable(name = "memberId") Long targetMemberId) {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerResponse.mannerKeywordResponseDTO result = mannerService.getMannerKeyword(memberId, targetMemberId);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/bad/{memberId}")
    @Operation(summary = "비매너 평가 조회 API", description = "회원이 실시한 비매너 평가를 조회하는 API 입니다.")
    @Parameter(name = "memberId", description = "회원이 실시한 비매너평가 대상의 id 입니다.")
    public ApiResponse<MannerResponse.badMannerKeywordResponseDTO> getBadMannerKeyword(
            @PathVariable(name = "memberId") Long targetMemberId) {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerResponse.badMannerKeywordResponseDTO result = mannerService.getBadMannerKeyword(memberId, targetMemberId);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping()
    @Operation(summary = "내가 받은 매너 평가 조회 API", description = "회원의 매너레벨과 회원이 받은 키워드를 조회하는 API 입니다.")
    public ApiResponse<MannerResponse.myMannerResponseDTO> getMyManner() {
        Long memberId = JWTUtil.getCurrentUserId();

        MannerResponse.myMannerResponseDTO result = mannerService.getMyManner(memberId);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "다른 유저의 매너 평가 조회 API", description = "다른 유저의 매너레벨과 키워드를 조회하는 API 입니다.")
    @Parameter(name = "memberId", description = "매너 평가 조회 대상의 id 입니다.")
    public ApiResponse<MannerResponse.mannerByIdResponseDTO> getMannerById(
            @PathVariable(name = "memberId") Long targetMemberId) {
        MannerResponse.mannerByIdResponseDTO result = mannerService.getMannerById(targetMemberId);

        return ApiResponse.onSuccess(result);
    }

}
