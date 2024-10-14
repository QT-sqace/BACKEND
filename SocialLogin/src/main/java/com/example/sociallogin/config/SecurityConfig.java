package com.example.sociallogin.config;

import com.example.sociallogin.Service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, ClientRegistrationRepository clientRegistrationRepository) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf, formLogin, httpBasic 설정을 해제 추후 필요시 설정
        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .formLogin(AbstractHttpConfigurer::disable);
        http
                .httpBasic(AbstractHttpConfigurer::disable);
        // 유저 인포 엔드포인트를 커스텀한 서비스로 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)));
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated());
        //하단의 커스텀 리포지토리를 적용
        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(Objects.requireNonNull(customAuthorizationRequestResolver(this.clientRegistrationRepository)))));
        return http.build();
    }

    /**
     * 지라의 경우 구글과 카카오와는 다르게 추가적인 파라미터를 요구하기 때문에
     * prompt,audience 파라미터를 추가해주는 resolver
     * @param clientRegistrationRepository 클라이언트 등록 리포지토리
     * @return resolver
     */
    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizer -> {
            String registrationId = customizer.build().getAttribute("registration_id");
            if ("jira".equals(registrationId)) {
                // Add custom properties for Jira login
                customizer.additionalParameters(params -> params.put("prompt", "consent"));
                customizer.additionalParameters(params -> params.put("audience", "api.atlassian.com"));
            }
        });

        return resolver;
    }

}

