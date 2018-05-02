package com.jnj.honeur.asset;

import com.jnj.honeur.catalogue.model.Study;
import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class StudyAssetRenderer extends BaseJSPAssetRenderer<Study> {

    private static final long GROUP_ID = 35205;

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyAssetRenderer.class);

    private Study study;

    public StudyAssetRenderer(Study study) {
        LOGGER.info("new StudyAssetRenderer for study with id: " + study.getId());
        this.study = study;
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        LOGGER.info("getJspPath: " + template);
        if (template.equals(TEMPLATE_FULL_CONTENT)) {
            request.setAttribute("study", study);
            return "/asset/study/" + template + ".jsp";
        } else {
            LOGGER.warn(String.format("No template %s found", template));
            return null;
        }
    }

    @Override
    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
                                 LiferayPortletResponse liferayPortletResponse) throws Exception {

        PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(
                getControlPanelPlid(liferayPortletRequest), StudyCataloguePortletKeys.StudyCatalogue,
                PortletRequest.RENDER_PHASE);
        portletURL.setParameter("mvcRenderCommandName", "/study-details");
        portletURL.setParameter("mvcPath", "/study-details.jsp");
        portletURL.setParameter("studyId", String.valueOf(study.getId()));
        portletURL.setParameter("showback", Boolean.FALSE.toString());

        LOGGER.info("getURLEdit: " + portletURL);

        return portletURL;
    }

    @Override
    public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
                                      LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) throws Exception {

        LOGGER.info("getURLViewInContext");
        try {
            long plid = PortalUtil.getPlidFromPortletId(GROUP_ID, StudyCataloguePortletKeys.StudyCatalogue);
            LOGGER.info("getURLViewInContext plid: " + plid);
            LOGGER.info("getURLViewInContext DEFAULT_PLID: " + LayoutConstants.DEFAULT_PLID);

            PortletURL portletURL;
            if (plid == LayoutConstants.DEFAULT_PLID) {
                LOGGER.info("DEFAULT_PLID");
                portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(liferayPortletRequest),
                        StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
            } else {
                portletURL = PortletURLFactoryUtil.create(liferayPortletRequest,
                        StudyCataloguePortletKeys.StudyCatalogue, plid, PortletRequest.RENDER_PHASE);
            }

            portletURL.setParameter("mvcRenderCommandName", "/study-details");
            portletURL.setParameter("mvcPath", "/study-details.jsp");
            portletURL.setParameter("studyId", String.valueOf(study.getId()));

            String currentUrl = PortalUtil.getCurrentURL(liferayPortletRequest);
            LOGGER.debug("getURLViewInContext redirect: " + currentUrl);
            portletURL.setParameter("redirect", currentUrl);

            LOGGER.info("getURLViewInContext portletURL: " + portletURL);

            return portletURL.toString();

        } catch (PortalException | SystemException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return noSuchEntryRedirect;
    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse,
                             WindowState windowState) throws Exception {

        String viewURL = super.getURLView(liferayPortletResponse, windowState);
        LOGGER.info("getURLView: '" + viewURL + "'");
        return viewURL;
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {
        LOGGER.info("include: template: " + template);
        request.setAttribute("study", study);
        request.setAttribute("HtmlUtil", HtmlUtil.getHtml());
        request.setAttribute("StringUtil", new StringUtil());
        return super.include(request, response, template);
    }

    @Override
    public boolean hasEditPermission(PermissionChecker permissionChecker) throws PortalException {
        // TODO create StudyPermission
        //return StudyPermission.contains(permissionChecker, study.getId(), ActionKeys.UPDATE);
        return false;
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) throws PortalException {
        // TODO create StudyPermission
        //return StudyPermission.contains(permissionChecker, study.getId(), ActionKeys.VIEW);
        return true;
    }

    @Override
    public Study getAssetObject() {
        return study;
    }

    @Override
    public long getGroupId() {
        return GROUP_ID;
    }

    @Override
    public long getUserId() {
        return study.getCreatorUserId();
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getUuid() {
        return study.getUuid();
    }

    @Override
    public String getClassName() {
        return Study.class.getName();
    }

    @Override
    public long getClassPK() {
        return study.getId();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        LOGGER.info("getSummary");
        return study.getDescription();
    }

    @Override
    public String getTitle(Locale locale) {
        LOGGER.info("getTitle");
        return study.getName();
    }

}
