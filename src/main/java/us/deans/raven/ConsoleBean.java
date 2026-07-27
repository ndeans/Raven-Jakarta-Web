package us.deans.raven;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
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

    @Inject
    private ConfigBean configBean;

    private List<RvnJob> uploads;
    private String authorFilter = "";
    private String keywordFilter = "";
    private int pageOffset = 0;
    private boolean hasNextPage = false;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        reload();
    }

    private boolean isFilterActive() {
        return (authorFilter != null && !authorFilter.isBlank())
                || (keywordFilter != null && !keywordFilter.isBlank());
    }

    private void reload() {
        int pageSize = configBean.getPageSize();
        try {
            List<RvnJob> results;
            if (isFilterActive()) {
                OppCurator curator = new OppCurator();
                results = curator.getFilteredUploads(authorFilter, keywordFilter, pageOffset, pageSize);
            } else {
                Maria_DAO mariaDao = new Maria_DAO();
                results = mariaDao.getMetaData(pageOffset, pageSize);
            }
            hasNextPage = results.size() > pageSize;
            uploads = hasNextPage ? results.subList(0, pageSize) : results;
            logger.info("ConsoleBean loaded {} uploads (offset={}, filterActive={})",
                    uploads.size(), pageOffset, isFilterActive());
        } catch (Exception ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    public void applyFilter() {
        pageOffset = 0;
        reload();
    }

    public void clearFilter() {
        authorFilter = "";
        keywordFilter = "";
        pageOffset = 0;
        reload();
    }

    public void nextPage() {
        pageOffset += configBean.getPageSize();
        reload();
    }

    public void previousPage() {
        pageOffset = Math.max(0, pageOffset - configBean.getPageSize());
        reload();
    }

    public boolean isHasNextPage() { return hasNextPage; }
    public boolean isHasPreviousPage() { return pageOffset > 0; }

    public List<RvnJob> getUploads() { return uploads; }
    public void setUploads(List<RvnJob> uploads) { this.uploads = uploads; }

    public String getAuthorFilter() { return authorFilter; }
    public void setAuthorFilter(String authorFilter) { this.authorFilter = authorFilter; }

    public String getKeywordFilter() { return keywordFilter; }
    public void setKeywordFilter(String keywordFilter) { this.keywordFilter = keywordFilter; }
}
