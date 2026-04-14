package us.deans.raven;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import us.deans.raven.processor.Maria_DAO;

import us.deans.raven.processor.RvnJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
@Named
@RequestScoped
public class ConsoleBean implements Serializable {

    private boolean submitted;
    private List<RvnJob> uploads;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConsoleBean() {
        Maria_DAO maria_dao = new Maria_DAO();
        try {
            uploads = maria_dao.getMetaData();
            logger.info("ConsoleBean initiated.");
        } catch (Exception ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    public void setUploads(List<RvnJob> uploads) {
        this.uploads = uploads;
    }
    public List<RvnJob> getUploads() { return uploads; }

    public boolean isSubmitted() {
        return submitted;
    }
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

}
