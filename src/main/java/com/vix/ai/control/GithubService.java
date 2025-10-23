package com.vix.ai.control;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.http.*;
import java.net.URI;

@ApplicationScoped
public class GithubService {

    @ConfigProperty(name = "github.token")
    String githubToken;

    public String getPullRequestDiff(String owner, String repo, int prNumber) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/" + owner + "/" + repo + "/pulls/" + prNumber))
                    .header("Accept", "application/vnd.github.v3.diff")
                    .header("Authorization", "Bearer " + githubToken)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }

    public void commentOnPullRequest(String owner, String repo, int prNumber, String comment) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"body\": \"" + comment.replace("\"", "'") + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.github.com/repos/" + owner + "/" + repo + "/issues/" + prNumber + "/comments"))
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
        }
    }
}
