package com.gamegoo.controller.member;

import com.gamegoo.apiPayload.ApiResponse;
import com.gamegoo.converter.MemberConverter;
import com.gamegoo.domain.member.Member;
import com.gamegoo.dto.member.MemberResponse;
import com.gamegoo.service.member.BlockService;
import com.gamegoo.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Block", description = "회원 차단 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member/block")
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "회원 차단 API", description = "대상 회원을 차단하는 API 입니다.")
    @Parameter(name = "memberId", description = "차단할 대상 회원의 id 입니다.")
    @PostMapping("/{memberId}")
    public ApiResponse<String> blockMember(@PathVariable(name = "memberId") Long targetMemberId) {
        Long memberId = JWTUtil.getCurrentUserId(); //헤더에 있는 jwt 토큰에서 id를 가져오는 코드
        blockService.blockMember(memberId, targetMemberId);

        return ApiResponse.onSuccess("회원 차단 성공");
    }

    @Operation(summary = "차단한 회원 목록 조회 API", description = "내가 차단한 회원의 목록을 조회하는 API 입니다.")
    @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해 주세요.")
    @GetMapping
    public ApiResponse<MemberResponse.blockListDTO> getBlockList(@RequestParam(name = "page") Integer page) {
        Long memberId = JWTUtil.getCurrentUserId();
        Page<Member> blockList = blockService.getBlockList(memberId, page - 1);

        return ApiResponse.onSuccess(MemberConverter.toBlockListDTO(blockList));
    }

    @Operation(summary = "회원 차단 해제 API", description = "해당 회원에 대한 차단을 해제하는 API 입니다.")
    @Parameter(name = "memberId", description = "차단을 해제할 대상 회원의 id 입니다.")
    @DeleteMapping("/{memberId}")
    public ApiResponse<String> unBlockMember(@PathVariable(name = "memberId") Long targetMemberId) {
        Long memberId = JWTUtil.getCurrentUserId();
        blockService.unBlockMember(memberId, targetMemberId);

        return ApiResponse.onSuccess("차단 해제 성공");
    }

    @Operation(summary = "차단 목록에서 해당 회원 삭제 API", description = "차단 목록에서 해당 회원을 삭제하는 API 입니다. (차단 해제 아님)")
    @Parameter(name = "memberId", description = "목록에서 삭제할 대상 회원의 id 입니다.")
    @DeleteMapping("/delete/{memberId}")
    public ApiResponse<String> deleteBlockMember(@PathVariable(name = "memberId") Long targetMemberId) {
        Long memberId = JWTUtil.getCurrentUserId();
        blockService.deleteBlockMember(memberId, targetMemberId);

        return ApiResponse.onSuccess("차단 목록에서 삭제 성공");
    }

}
