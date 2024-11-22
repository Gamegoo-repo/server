package com.gamegoo.scheduler;

import com.gamegoo.apiPayload.code.status.ErrorStatus;
import com.gamegoo.apiPayload.exception.handler.MemberHandler;
import com.gamegoo.domain.champion.Champion;
import com.gamegoo.domain.champion.MemberChampion;
import com.gamegoo.domain.chat.Chat;
import com.gamegoo.domain.matching.MatchingRecord;
import com.gamegoo.domain.matching.MatchingStatus;
import com.gamegoo.domain.member.Tier;
import com.gamegoo.dto.member.RiotResponse;
import com.gamegoo.repository.matching.MatchingRecordRepository;
import com.gamegoo.repository.member.ChampionRepository;
import com.gamegoo.repository.member.MemberChampionRepository;
import com.gamegoo.repository.member.MemberRepository;
import com.gamegoo.service.chat.ChatCommandService;
import com.gamegoo.service.chat.ChatQueryService;
import com.gamegoo.service.socket.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final MatchingRecordRepository matchingRecordRepository;
    private final MemberRepository memberRepository;
    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;
    private final SocketService socketService;
    private final ChampionRepository championRepository;
    private final MemberChampionRepository memberChampionRepository;

    private static final Long MANNER_MESSAGE_TIME = 60L; // 매칭 성공 n초 이후의 엔티티 조회함
    private static final String MANNER_SYSTEM_MESSAGE = "매칭은 어떠셨나요? 상대방의 매너를 평가해주세요!";

    // RIOT
    private final RestTemplate restTemplate;

    @Value("${spring.riot.api.key}")
    private String riotAPIKey;

    private static final String RIOT_ACCOUNT_API_URL_TEMPLATE = "https://asia.api.riotgames" +
            ".com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s";
    private static final String RIOT_SUMMONER_API_URL_TEMPLATE = "https://kr.api.riotgames" +
            ".com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s";
    private static final String RIOT_LEAGUE_API_URL_TEMPLATE = "https://kr.api.riotgames" +
            ".com/lol/league/v4/entries/by-summoner/%s?api_key=%s";
    private static final String RIOT_MATCH_API_URL_TEMPLATE = "https://asia.api.riotgames" +
            ".com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%s&api_key=%s";
    private static final String RIOT_MATCH_INFO_API_URL_TEMPLATE = "https://asia.api.riotgames" +
            ".com/lol/match/v5/matches/%s?api_key=%s";
    private static final Map<String, Integer> romanToIntMap = new HashMap<>();

    static {
        romanToIntMap.put("I", 1);
        romanToIntMap.put("II", 2);
        romanToIntMap.put("III", 3);
        romanToIntMap.put("IV", 4);
    }

    /**
     * 매칭 성공 1시간이 경과한 경우, 두 사용자에게 매너평가 시스템 메시지 전송
     */
    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000) // 60 * 60초 주기로 실행
    public void mannerSystemMessageRun() {
//        log.info("scheduler start");

        // 매칭 성공 1시간이 경과된 matchingRecord 엔티티 조회
        LocalDateTime updatedTime = LocalDateTime.now().minusSeconds(MANNER_MESSAGE_TIME);
        List<MatchingRecord> matchingRecordList =
                matchingRecordRepository.findByStatusAndMannerMessageSentAndUpdatedAtBefore(MatchingStatus.SUCCESS,
                        false, updatedTime);

        matchingRecordList.forEach(matchingRecord -> {
            chatQueryService.getChatroomByMembers(
                    matchingRecord.getMember(),
                    matchingRecord.getTargetMember()
            ).ifPresentOrElse(
                    chatroom -> {
                        // 시스템 메시지 생성 및 db 저장
                        Chat createdChat = chatCommandService.createAndSaveSystemChat(chatroom,
                                matchingRecord.getMember(), MANNER_SYSTEM_MESSAGE, null, 1);

                        // 매너 평가 메시지 전송 여부 업데이트
                        matchingRecord.updateMannerMessageSent(true);

                        // socket 서버에게 메시지 전송 API 요청
                        socketService.sendSystemMessage(matchingRecord.getMember().getId(), chatroom.getUuid(),
                                MANNER_SYSTEM_MESSAGE, createdChat.getTimestamp());
                    },
                    () -> log.info("Chatroom not found, member ID: {}, target member ID: {}",
                            matchingRecord.getMember().getId(), matchingRecord.getTargetMember().getId()));
        });
    }

    /**
     * 07:00 모든 사용자 정보 업데이트
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void updateAllUserRiotInformation() {
        log.info("Riot 업데이트 - 시작");

        memberChampionRepository.deleteAllInBatch();

        memberRepository.findAll().forEach(member -> {
            String gameName = member.getGameName();
            String tag = member.getTag();

            if (gameName.equals("SYSTEM")) {
                return;
            }
            try {
                // 1. puuid 조회
                String url = String.format(RIOT_ACCOUNT_API_URL_TEMPLATE, gameName, tag, riotAPIKey);
                RiotResponse.RiotAccountDTO accountResponse = null;
                accountResponse = restTemplate.getForObject(url, RiotResponse.RiotAccountDTO.class);
                String puuid = accountResponse.getPuuid();

                // 2. encryptedSummonerId 조회
                String summonerUrl = String.format(RIOT_SUMMONER_API_URL_TEMPLATE, puuid, riotAPIKey);
                RiotResponse.RiotSummonerDTO summonerResponse = null;
                summonerResponse = restTemplate.getForObject(summonerUrl, RiotResponse.RiotSummonerDTO.class);
                String encryptedSummonerId = summonerResponse.getId();

                // 3. tier, rank, winrate 조회
                //    (1) account id로 티어, 랭크, 불러오기
                String leagueUrl = String.format(RIOT_LEAGUE_API_URL_TEMPLATE, encryptedSummonerId, riotAPIKey);
                RiotResponse.RiotLeagueEntryDTO[] leagueEntries = restTemplate.getForObject(leagueUrl,
                        RiotResponse.RiotLeagueEntryDTO[].class);

                //    (2) tier, rank, gameCount 정보 저장
                for (RiotResponse.RiotLeagueEntryDTO entry : leagueEntries) {
                    // 솔랭일 경우에만 저장
                    if ("RANKED_SOLO_5x5".equals(entry.getQueueType())) {
                        int wins = entry.getWins();
                        int losses = entry.getLosses();
                        int gameCount = wins + losses;
                        double winrate = (double) wins / (wins + losses);
                        winrate = Math.round(winrate * 1000) / 10.0;
                        Tier tier = Tier.valueOf(entry.getTier().toUpperCase());
                        Integer rank = romanToIntMap.get(entry.getRank());

                        // DB에 저장
                        member.updateRiotDetails(tier, rank, winrate, gameCount);
                        break;
                    }
                }

                if (member.getTier() == null) {
                    member.updateRiotDetails(Tier.UNRANKED, 0, 0.0, 0);
                }

                // 4. 최근 플레이한 사용자 3개 불러오기
                List<Integer> recentChampionIds = null;
                int count = 20;

                // 4-1. 최근 플레이한 챔피언 리스트 조회
                while ((recentChampionIds == null || recentChampionIds.size() < 3) && count <= 100) {
                    String matchUrl = String.format(RIOT_MATCH_API_URL_TEMPLATE, puuid, count, riotAPIKey);
                    String[] matchIds = restTemplate.getForObject(matchUrl, String[].class);
                    List<String> recentMatchIds = Arrays.asList(Objects.requireNonNull(matchIds));

                    recentChampionIds = recentMatchIds.stream()
                            .map(matchId -> getChampionIdFromMatch(matchId, gameName))
                            .filter(championId -> championId < 1000)
                            .toList();

                    if (recentChampionIds.size() < 3) {
                        count += 10; // count를 10 증가시켜서 다시 시도
                    }
                }

                // 4-2. 해당 캐릭터 중 많이 사용한 캐릭터 세 개 저장하기
                //      (1) 챔피언 사용 빈도 계산
                Map<Integer, Long> championFrequency = recentChampionIds.stream()
                        .collect(Collectors.groupingBy(championId -> championId, Collectors.counting()));

                //      (2) 빈도를 기준으로 정렬하여 상위 3개의 챔피언 추출
                List<Integer> top3Champions = championFrequency.entrySet().stream()
                        .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .toList();

                // 5. DB에 저장
                if (top3Champions != null) {
                    top3Champions
                            .forEach(championId -> {
                                Champion champion = championRepository.findById(Long.valueOf(championId))
                                        .orElseThrow(() -> new MemberHandler(ErrorStatus.CHAMPION_NOT_FOUND));

                                MemberChampion memberChampion = MemberChampion.builder()
                                        .champion(champion)
                                        .build();

                                memberChampion.setMember(member);
                            });
                }

            } catch (Exception e) {
                log.warn("Riot 업데이트 - 라이엇 정보 연동에 문제 발생 : ", e);
            }

            memberRepository.save(member);
        });
        log.info("Riot 업데이트 - 완료");
    }

    public Integer getChampionIdFromMatch(String matchId, String gameName) {
        // 매치 정보 가져오기
        String matchInfoUrl = String.format(RIOT_MATCH_INFO_API_URL_TEMPLATE, matchId, riotAPIKey);
        RiotResponse.MatchDTO matchResponse = restTemplate.getForObject(matchInfoUrl, RiotResponse.MatchDTO.class);

        // 참가자 정보에서 gameName과 일치하는 사용자의 champion ID 찾기
        return matchResponse.getInfo().getParticipants().stream()
                .filter(participant -> gameName.equals(participant.getRiotIdGameName()))
                .map(RiotResponse.ParticipantDTO::getChampionId)
                .findFirst()
                .orElseGet(() -> {
                    log.info("Riot 업데이트 - 최근 선호 챔피언 값 중 id가 없는 챔피언이 있습니다.");
                    return null;
                });
    }

}
