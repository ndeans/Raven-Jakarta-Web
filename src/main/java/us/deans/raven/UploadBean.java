package us.deans.raven;

import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.deans.raven.processor.Curator;
import us.deans.raven.processor.RvnPost;
import us.deans.raven.processor.OppCurator;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UploadBean implements Serializable {

    @ManagedProperty(value = "#{param.jid}")
    private long upload_id;

    private String topic_id;
    private String topic_title;
    private List<RvnPost> postList;
    private boolean isUploadIdSet = false;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        logger.info("UploadBean initialized.");
    }

    public void loadPosts() {
        if (!isUploadIdSet) {
            logger.info("upload_id not set yet, delaying loadPost call.");
            return;
        }
        Curator curator = new OppCurator();
        try {
            postList = curator.getPostList(upload_id);
            topic_id = "";
            topic_title = "";
            logger.info("Loaded {} posts for for upload: {} ", postList.size(), upload_id );
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public long getUpload_id() {
        return upload_id;
    }
    public void setUpload_id(long upload_id) {
        logger.info("Setting upload_id to: " + upload_id);
        this.upload_id = upload_id;
        isUploadIdSet = true;
        loadPosts();   // as soon as we know the upload id, we can load the post list.
    }
    public void setPostList(List<RvnPost> postList) {
        this.postList = postList;
    }
    public List<RvnPost> getPostList() {
        return postList;
    }

    public boolean isUploadIdSet() {
        return isUploadIdSet;
    }
    public void setUploadIdSet(boolean uploadIdSet) {
        isUploadIdSet = uploadIdSet;
    }

}