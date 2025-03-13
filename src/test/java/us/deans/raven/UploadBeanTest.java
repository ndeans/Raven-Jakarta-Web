package us.deans.raven;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import us.deans.raven.processor.RvnPost;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadBeanTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private UploadBean uploadBean;
/*
    @BeforeEach
    public void setup() {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequestParameterMap()).thenReturn(Map.of("jid","80"));
    }

    @Test
    @Disabled
    public void test1_Init() {
        if (uploadBean.isUploadIdSet()) {
            uploadBean.loadPosts();
            assertEquals(80L, uploadBean.getUpload_id());
        } else {
            assertEquals(0, uploadBean.getUpload_id());
        }
    }
    @Test
    @Disabled
    public void test2_getPostList() {
        if (uploadBean.isUploadIdSet()) {
            List<RvnPost> postList = uploadBean.getPostList();
            assertNotNull(postList);
        } else {
            fail("upload_id is not set.");
        }

    }

 */
}

