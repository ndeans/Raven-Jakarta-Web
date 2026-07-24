package us.deans.raven;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.deans.raven.processor.Curator;
import us.deans.raven.processor.MongoDao;
import us.deans.raven.processor.OppCurator;
import us.deans.raven.processor.R7Conversation;
import us.deans.raven.processor.R7Service;
import us.deans.raven.processor.RvnPost;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class PostAnalyzerBean implements Serializable {

    private String targetPostId;
    private List<R7Conversation> conversations;
    private R7Conversation selectedConversation;
    private String notFoundMessage;

    private static final Logger logger = LoggerFactory.getLogger(PostAnalyzerBean.class);

    public void loadByPostId(String postId) {
        this.targetPostId = postId;
        this.selectedConversation = null;
        this.notFoundMessage = null;

        Curator curator = new OppCurator();
        Optional<RvnPost> post;
        try {
            post = curator.findPostById(postId);
        } catch (Exception e) {
            logger.error("PostAnalyzerBean.loadByPostId: error finding post_id={}: {}", postId, e.getMessage());
            notFoundMessage = "No post found with id " + postId;
            return;
        }

        if (post.isEmpty()) {
            notFoundMessage = "No post found with id " + postId;
            return;
        }

        String uploadId = String.valueOf(post.get().getUpload_id());
        conversations = new R7Service(new MongoDao()).execute(uploadId);

        selectedConversation = conversations.stream()
                .filter(c -> c.getPosts().stream()
                        .anyMatch(p -> p.getPostId().equals(postId)))
                .findFirst()
                .orElse(null);

        if (selectedConversation == null) {
            // Post exists but R7 discarded it as a standalone comment (width = 0)
            notFoundMessage = "This post was not part of any reconstructed conversation "
                    + "(likely a standalone comment with no replies or quoted up-link).";
        }
    }

    public String getTargetPostId() { return targetPostId; }
    public void setTargetPostId(String targetPostId) { this.targetPostId = targetPostId; }

    public R7Conversation getSelectedConversation() { return selectedConversation; }

    public String getNotFoundMessage() { return notFoundMessage; }
}
