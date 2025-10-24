package com.vix.ai.boundary;

import com.vix.ai.control.GithubService;
import com.vix.ai.control.OpenAiService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
public class AiReviewerResource {

    @Inject
    OpenAiService openAiService;

    @Inject
    GithubService githubService;

    @POST
    public Response review(@QueryParam("owner") String owner,
                           @QueryParam("repo") String repo,
                           @QueryParam("pr") int prNumber) {

        String diff = githubService.getPullRequestDiff(owner, repo, prNumber);
        String review = openAiService.analyzeCode(diff);
        githubService.commentOnPullRequest(owner, repo, prNumber, review);

        return Response.ok("{\"status\":\"reviewed\"}").build();
    }
}
