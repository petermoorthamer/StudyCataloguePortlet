package com.jnj.honeur.asset;


import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.model.Study;
import com.jnj.honeur.model.StudyModel;
import com.jnj.honeur.service.StudyServiceFacade;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.ServletContext;

@Component(immediate = true,
        property = {"javax.portlet.name=" + StudyCataloguePortletKeys.StudyCatalogue},
        service = AssetRendererFactory.class
)
public class StudyModelAssetRendererFactory extends BaseAssetRendererFactory<StudyModel> {
    public static final String CLASS_NAME = Study.class.getName();
    public static final String TYPE = "studymodel"; // @TODO: figure out what this means.
    private static final boolean LINKABLE = true;
    private StudyServiceFacade studyServiceFacade;
    private ServletContext servletContext;

    public StudyModelAssetRendererFactory() {
        setClassName(CLASS_NAME);
        setLinkable(LINKABLE);
        setPortletId(StudyCataloguePortletKeys.StudyCatalogue);
        setSearchable(true);
        setSelectable(true);
    }

    // @TODO: check if @Reference target is correct.
    @Reference(target = "(osgi.web.symbolicname=com.jnj.honeur.porlet)", unbind = "-")
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Reference(unbind = "-")
    protected void setStudyServiceFacade(StudyServiceFacade studyServiceFacade) {
        this.studyServiceFacade = studyServiceFacade;
    }

    @Override
    public AssetRenderer<StudyModel> getAssetRenderer(long classPK, int type) {
        StudyModel studyModel = studyServiceFacade.findStudyModelById(classPK);
        StudyModelAssetRenderer studyModelAssetRenderer = new StudyModelAssetRenderer(studyModel);
        studyModelAssetRenderer.setAssetRendererType(type);
        studyModelAssetRenderer.setServletContext(servletContext);
        return studyModelAssetRenderer;
    }

    @Override
    public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, long classTypeId) throws PortalException {
        PortletURL portletURL = null;
        try {
//            ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);
//            portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(themeDisplay), StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
            portletURL = liferayPortletResponse.createLiferayPortletURL(PortalUtil.getControlPanelPlid(liferayPortletRequest), StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
            portletURL.setParameter("mvcRenderCommandName", StudyCataloguePortletKeys.StudyModelAssetURLEdit);
            portletURL.setParameter("showback", Boolean.FALSE.toString());
        } catch (PortalException e) { // @TODO: add logger.
        }
        return portletURL;
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, long classPK, String actionId) throws Exception {
        return super.hasPermission(permissionChecker, classPK, actionId);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean isLinkable() {
        return LINKABLE;
    }

    @Override
    public String getIconCssClass() {
        return "studies";
    }
}
