package us.deans.raven;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import us.deans.raven.processor.Maria_DAO;
import us.deans.raven.processor.OppCurator;
import us.deans.raven.processor.RvnJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class ConsoleBean implements Serializable {

    private List<RvnJob> uploads;
    private String authorFilter = "";
    private String keywordFilter = "";
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        loadAll();
    }

    private void loadAll() {
        Maria_DAO mariaDao = new Maria_DAO();
        try {
            uploads = mariaDao.getMetaData();
            logger.info("ConsoleBean loaded {} uploads.", uploads.size());
        } catch (Exception ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    public void applyFilter() {
        OppCurator curator = new OppCurator();
        try {
            uploads = curator.getFilteredUploads(authorFilter, keywordFilter);
            logger.info("Filter applied — author='{}' keyword='{}' — {} results",
                    authorFilter, keywordFilter, uploads.size());
        } catch (Exception ex) {
            logger.error("Filter failed", ex);
        }
    }

    public void clearFilter() {
        authorFilter = "";
        keywordFilter = "";
        loadAll();
    }

    public List<RvnJob> getUploads() { return uploads; }
    public void setUploads(List<RvnJob> uploads) { this.uploads = uploads; }

    public String getAuthorFilter() { return authorFilter; }
    public void setAuthorFilter(String authorFilter) { this.authorFilter = authorFilter; }

    public String getKeywordFilter() { return keywordFilter; }
    public void setKeywordFilter(String keywordFilter) { this.keywordFilter = keywordFilter; }
}
