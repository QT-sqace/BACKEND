package com.example.jiralink.service;

public interface JiraAuthService {

    /**
     * Jira 인증 코드로 액세스 토큰을 요청합니다.
     *
     * @param userId 사용자 ID
     * @param code   인증 코드
     * @return 액세스 토큰
     */
    String requestAccessToken(Long userId, String code);

    /**
     * Jira Cloud ID를 조회합니다.
     * (OAuth에서 제공되는 리소스 엔드포인트를 통해 조회)
     *
     * @param accessToken 액세스 토큰
     * @return Cloud ID
     */
    String fetchCloudId(String accessToken);

    /**
     * Jira API를 통해 사용자 정보를 가져옵니다.
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 정보 JSON 문자열
     * @throws Exception 예외 발생 시
     */
    String getUserInfo(String accessToken) throws Exception;

    /**
     * 사용자 OAuth 정보를 업데이트합니다.
     * 액세스 토큰과 CloudId를 저장하거나 업데이트합니다.
     *
     * @param userId      사용자 ID
     * @param accessToken 액세스 토큰
     * @param cloudId     Jira Cloud ID
     */
    void updateAccessTokenAndCloudId(Long userId, String accessToken, String cloudId);
}
