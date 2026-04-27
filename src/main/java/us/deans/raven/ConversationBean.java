package us.deans.raven;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.deans.raven.processor.MongoDao;
import us.deans.raven.processor.R7Conversation;
import us.deans.raven.processor.R7Post;
import us.deans.raven.processor.R7Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Named
@SessionScoped
public class ConversationBean implements Serializable {

    private String upload_id;
    private String topicTitle;
    private List<R7Conversation> conversations;
    private List<R7Post> contextPosts;
    private int selectedIndex;

    private static final Logger logger = LoggerFactory.getLogger(ConversationBean.class);

    public void loadConversations() {
        if (upload_id == null || upload_id.isBlank()) {
            logger.warn("ConversationBean.loadConversations called with no upload_id");
            return;
        }
        if (conversations != null) {
            logger.info("ConversationBean.loadConversations: already loaded for upload_id={}, skipping", upload_id);
            return;
        }
        logger.info("ConversationBean.loadConversations: starting for upload_id={}", upload_id);
        try {
            MongoDao mongoDao = new MongoDao();
            conversations = new R7Service(mongoDao).execute(upload_id);
            logger.info("ConversationBean.loadConversations: {} conversation(s) loaded for upload_id={}", conversations.size(), upload_id);

            List<R7Post> allPosts = mongoDao.getPostsByUploadId(upload_id);
            contextPosts = new ArrayList<>();
            for (R7Conversation conv : conversations) {
                R7Post context = conv.getPosts().isEmpty() ? null
                        : findContextPost(conv.getPosts().get(0), allPosts);
                contextPosts.add(context);
                if (context != null) {
                    logger.info("ConversationBean: context post_id={} for conversation root post_id={}",
                            context.getPostId(), conv.getPosts().get(0).getPostId());
                }
            }
        } catch (Exception ex) {
            logger.error("ConversationBean.loadConversations: error for upload_id={}: {}", upload_id, ex.getMessage());
        }
    }

    private R7Post findContextPost(R7Post rootPost, List<R7Post> allPosts) {
        if (rootPost.getHtml() == null || rootPost.getHtml().isBlank()) return null;

        Document doc = Jsoup.parse(rootPost.getHtml());
        Element outerDiv = doc.selectFirst("div.smalltext");
        if (outerDiv == null) return null;

        String outerText = outerDiv.ownText().trim();
        if (!outerText.endsWith("wrote:")) return null;

        String quotedAuthor = outerText.substring(0, outerText.length() - "wrote:".length()).trim();
        if (quotedAuthor.isBlank()) return null;

        long rootPostId;
        try { rootPostId = Long.parseLong(rootPost.getPostId()); }
        catch (NumberFormatException e) { return null; }

        return allPosts.stream()
                .filter(p -> quotedAuthor.equals(p.getAuthor()) && p.getPostId() != null)
                .filter(p -> {
                    try { return Long.parseLong(p.getPostId()) < rootPostId; }
                    catch (NumberFormatException e) { return false; }
                })
                .max(Comparator.comparingLong(p -> {
                    try { return Long.parseLong(p.getPostId()); }
                    catch (NumberFormatException e) { return Long.MIN_VALUE; }
                }))
                .orElse(null);
    }

    public R7Conversation getSelectedConversation() {
        if (conversations == null || selectedIndex < 0 || selectedIndex >= conversations.size()) return null;
        return conversations.get(selectedIndex);
    }

    public R7Post getContextPost() {
        if (contextPosts == null || selectedIndex < 0 || selectedIndex >= contextPosts.size()) return null;
        return contextPosts.get(selectedIndex);
    }

    public String getUpload_id() { return upload_id; }

    public void setUpload_id(String upload_id) {
        if (this.upload_id != null && !this.upload_id.equals(upload_id)) {
            conversations = null;
            contextPosts = null;
            topicTitle = null;
        }
        this.upload_id = upload_id;
    }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public List<R7Conversation> getConversations() { return conversations; }
    public void setConversations(List<R7Conversation> conversations) { this.conversations = conversations; }

    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int selectedIndex) { this.selectedIndex = selectedIndex; }
}
