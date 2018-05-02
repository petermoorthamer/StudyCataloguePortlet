package com.jnj.honeur.asset;

import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.ServletContext;

@Component(
        immediate = true,
        property = "javax.portlet.name=" + StudyCataloguePortletKeys.StudyCatalogue,
        service = AssetRendererFactory.class
)
public class StudyAssetRendererFactory extends BaseAssetRendererFactory<Study> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyAssetRendererFactory.class);

    private static final boolean LINKABLE = true;
    private static final String CLASS_NAME = Study.class.getName();
    private static final String TYPE = "study";

    private StudyServiceFacade studyServiceFacade;
    private ServletContext servletContext;

    public StudyAssetRendererFactory() {
        LOGGER.info("*** new StudyAssetRendererFactory ***");
        setClassName(CLASS_NAME);
        setLinkable(LINKABLE);
        setPortletId(StudyCataloguePortletKeys.StudyCatalogue);
        setSearchable(true);
        setSelectable(true);
    }

    @Reference(target = "(osgi.web.symbolicname=com.jnj.honeur)", unbind = "-")
    public void setServletContext(ServletContext servletContext) {
        LOGGER.info("setServletContext: " + servletContext);
        this.servletContext = servletContext;
    }

    @Reference(unbind = "-")
    public void setStudyServiceFacade(StudyServiceFacade studyServiceFacade) {
        LOGGER.info("setStudyServiceFacade: " + studyServiceFacade);
        this.studyServiceFacade = studyServiceFacade;
    }

    @Override
    public AssetRenderer<Study> getAssetRenderer(long classPK, int type) throws PortalException {
        LOGGER.info("getAssetRenderer for study with id: " + classPK);
        Study study = studyServiceFacade.findStudyById(classPK);

        StudyAssetRenderer studyAssetRenderer = new StudyAssetRenderer(study);

        studyAssetRenderer.setAssetRendererType(type);
        studyAssetRenderer.setServletContext(servletContext);

        return studyAssetRenderer;
    }

    @Override
    public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
                                LiferayPortletResponse liferayPortletResponse, long classTypeId) {

        LOGGER.info("getURLAdd");
        PortletURL portletURL = null;

        try {
            ThemeDisplay themeDisplay = (ThemeDisplay)
                    liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

            portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(themeDisplay),
                    StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
            portletURL.setParameter("mvcRenderCommandName", "/study-details");
            portletURL.setParameter("showback", Boolean.FALSE.toString());
        } catch (PortalException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return portletURL;
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public String getType() {
        LOGGER.info("getType " + TYPE);
        return TYPE;
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker,
                                 long classPK, String actionId) throws Exception {

        LOGGER.info("hasPermission action: " + actionId);
        //Study study = studyServiceFacade.findStudyById(classPK);
        //return StudyPermission.contains(permissionChecker, study, actionId);
        return true;
    }

    @Override
    public boolean isLinkable() {
        return LINKABLE;
    }

    @Override
    public String getIconCssClass() {
        return "bookmarks";
    }

}
