package com.example.graph.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public class AuthSecurityProperties {
    private String expectedIssuer;
    private List<String> expectedAudience = new ArrayList<>();
    private boolean permitPublic = true;

    public String getExpectedIssuer() {
        return expectedIssuer;
    }

    public void setExpectedIssuer(String expectedIssuer) {
        this.expectedIssuer = expectedIssuer;
    }

    public List<String> getExpectedAudience() {
        return expectedAudience;
    }

    public void setExpectedAudience(List<String> expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    public boolean isPermitPublic() {
        return permitPublic;
    }

    public void setPermitPublic(boolean permitPublic) {
        this.permitPublic = permitPublic;
    }
}
