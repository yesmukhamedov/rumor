package com.example.graph.controller.auth;

import com.example.graph.auth.AuthServerClient;
import com.example.graph.auth.dto.AuthStartRequest;
import com.example.graph.auth.dto.AuthVerifyRequest;
import com.example.graph.auth.dto.LogoutRequest;
import com.example.graph.auth.dto.RefreshRequest;
import com.example.graph.auth.dto.AuthVerifyResponse;
import com.example.graph.auth.dto.OtpChallengeResponse;
import com.example.graph.auth.dto.TokenResponse;
import com.example.graph.security.TokenCookieService;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Controller
public class AuthController {
    private final AuthServerClient authServerClient;
    private final TokenCookieService tokenCookieService;

    public AuthController(AuthServerClient authServerClient, TokenCookieService tokenCookieService) {
        this.authServerClient = authServerClient;
        this.tokenCookieService = tokenCookieService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model) {
        if (StringUtils.hasText(error)) {
            model.addAttribute("error", error);
        }
        return "login";
    }

    @PostMapping("/otp/start")
    public String startOtp(@RequestParam("phoneNumber") String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return "redirect:/login?error=" + encode("Phone number is required.");
        }
        try {
            OtpChallengeResponse response = authServerClient.start(new AuthStartRequest(phoneNumber.trim()));
            if (response == null || !StringUtils.hasText(response.getChallengeId())) {
                return "redirect:/login?error=" + encode("Auth server did not return a challenge id.");
            }
            String encoded = UriUtils.encodeQueryParam(response.getChallengeId(), StandardCharsets.UTF_8);
            return "redirect:/otp/verify?challengeId=" + encoded;
        } catch (WebClientResponseException ex) {
            return "redirect:/login?error=" + encode("Failed to start OTP challenge.");
        }
    }

    @GetMapping("/otp/verify")
    public String verifyPage(@RequestParam(value = "challengeId", required = false) String challengeId,
                             @RequestParam(value = "error", required = false) String error,
                             Model model) {
        if (!StringUtils.hasText(challengeId)) {
            return "redirect:/login?error=Missing+challenge";
        }
        model.addAttribute("challengeId", challengeId);
        if (StringUtils.hasText(error)) {
            model.addAttribute("error", error);
        }
        return "otp-verify";
    }

    @PostMapping("/otp/verify")
    public String verifyOtp(@RequestParam("challengeId") String challengeId,
                            @RequestParam("otp") String otp,
                            HttpServletResponse response) {
        if (!StringUtils.hasText(challengeId) || !StringUtils.hasText(otp)) {
            String encoded = UriUtils.encodeQueryParam(challengeId, StandardCharsets.UTF_8);
            return "redirect:/otp/verify?challengeId=" + encoded + "&error=" + encode("OTP code is required.");
        }
        try {
            AuthVerifyResponse verifyResponse = authServerClient.verify(new AuthVerifyRequest(challengeId, otp.trim()));
            if (verifyResponse == null || !StringUtils.hasText(verifyResponse.getAccessToken())) {
                String encoded = UriUtils.encodeQueryParam(challengeId, StandardCharsets.UTF_8);
                return "redirect:/otp/verify?challengeId=" + encoded + "&error="
                    + encode("Auth server did not return tokens.");
            }
            HttpHeaders headers = new HttpHeaders();
            tokenCookieService.setAccessToken(headers, verifyResponse.getAccessToken(),
                verifyResponse.getExpiresInSeconds());
            if (StringUtils.hasText(verifyResponse.getRefreshToken())) {
                tokenCookieService.setRefreshToken(headers, verifyResponse.getRefreshToken());
            }
            applyHeaders(response, headers);
            return "redirect:/admin/nodes";
        } catch (WebClientResponseException ex) {
            String encoded = UriUtils.encodeQueryParam(challengeId, StandardCharsets.UTF_8);
            return "redirect:/otp/verify?challengeId=" + encoded + "&error="
                + encode("Invalid OTP or challenge.");
        }
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = TokenCookieService.REFRESH_TOKEN_COOKIE,
        required = false) String refreshToken,
                                        HttpServletResponse response) {
        if (!StringUtils.hasText(refreshToken)) {
            return ResponseEntity.status(401).build();
        }
        try {
            TokenResponse tokenResponse = authServerClient.refresh(new RefreshRequest(refreshToken));
            if (tokenResponse == null || !StringUtils.hasText(tokenResponse.getAccessToken())) {
                return ResponseEntity.status(401).build();
            }
            HttpHeaders headers = new HttpHeaders();
            tokenCookieService.setAccessToken(headers, tokenResponse.getAccessToken(),
                tokenResponse.getExpiresInSeconds());
            if (StringUtils.hasText(tokenResponse.getRefreshToken())) {
                tokenCookieService.setRefreshToken(headers, tokenResponse.getRefreshToken());
            }
            applyHeaders(response, headers);
            return ResponseEntity.noContent().build();
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(name = TokenCookieService.REFRESH_TOKEN_COOKIE, required = false)
                         String refreshToken,
                         HttpServletResponse response) {
        if (StringUtils.hasText(refreshToken)) {
            try {
                authServerClient.logout(new LogoutRequest(refreshToken));
            } catch (WebClientResponseException ex) {
                // ignore logout failures
            }
        }
        HttpHeaders headers = new HttpHeaders();
        tokenCookieService.clearTokens(headers);
        applyHeaders(response, headers);
        return "redirect:/login";
    }

    private void applyHeaders(HttpServletResponse response, HttpHeaders headers) {
        headers.forEach((name, values) -> values.forEach(value -> response.addHeader(name, value)));
    }

    private String encode(String value) {
        return UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8);
    }
}
