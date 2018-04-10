package com.jnj.honeur.asset;

import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.jnj.honeur.model.Study;
import com.jnj.honeur.model.StudyModel;
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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class StudyModelAssetRenderer extends BaseJSPAssetRenderer<StudyModel> {
    private StudyModel studyModel;

    public StudyModelAssetRenderer(StudyModel studyModel) {
        this.studyModel = studyModel;
    }

    @Override
    public boolean hasEditPermission(PermissionChecker permissionChecker) throws PortalException {
        return true; // @TODO: implement PermissionChecker
    }

    @Override
    public boolean hasViewPermission(PermissionChecker permissionChecker) throws PortalException {
        return true; // @TODO: implement PermissionChecker
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_FULL_CONTENT)) {
            request.setAttribute("sm_studymodel", studyModel);
            return "/asset/studymodel/" + template + ".jsp";
        }
        return null;
    }

    @Override
    public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) throws Exception {
        PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(liferayPortletRequest), StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
        portletURL.setParameter("mvcRenderCommandname", StudyCataloguePortletKeys.StudyModelAssetURLEdit);
        portletURL.setParameter("studyModelId", String.valueOf(studyModel.getId()));
        portletURL.setParameter("showback", Boolean.FALSE.toString());
        return portletURL;
    }

    @Override
    public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) throws Exception {
        try {
            Long plid = PortalUtil.getPlidFromPortletId(studyModel.getGroupId(), StudyCataloguePortletKeys.StudyCatalogue);
            PortletURL portletURL;
            if (plid == LayoutConstants.DEFAULT_PLID) {
                portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(liferayPortletRequest), StudyCataloguePortletKeys.StudyCatalogue, PortletRequest.RENDER_PHASE);
            } else {
                portletURL = PortletURLFactoryUtil.create(liferayPortletRequest, StudyCataloguePortletKeys.StudyCatalogue, plid, PortletRequest.RENDER_PHASE);
            }
            portletURL.setParameter("mvcRenderCommandName", StudyCataloguePortletKeys.StudyModelAssetURLList);
            portletURL.setParameter("studyModelId", String.valueOf(studyModel.getId()));
            portletURL.setParameter("redirect", PortalUtil.getCurrentURL(liferayPortletRequest));
            return portletURL.toString();
        } catch (PortalException e) { // @TODO: add logger.
        } catch (SystemException e) { // @TODO: add logger.
        }
        return noSuchEntryRedirect;
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {
        request.setAttribute("STUDYMODEL", studyModel);
        request.setAttribute("HtmlUtil", HtmlUtil.getHtml());
        request.setAttribute("StringUtil", new StringUtil());
        return super.include(request, response, template);
    }

    @Override
    public String getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) throws Exception {
        return super.getURLView(liferayPortletResponse, windowState);
    }

    @Override
    public StudyModel getAssetObject() {
        return studyModel;
    }

    @Override
    public long getGroupId() {
        return studyModel.getGroupId();
    }

    @Override
    public long getUserId() {
        return studyModel.getUserId();
    }

    @Override
    public String getUserName() {
        return studyModel.getUserName();
    }

    @Override
    public String getUuid() {
        return studyModel.getUuid();
    }

    @Override
    public String getClassName() {
        return Study.class.getName();
    }

    @Override
    public long getClassPK() {
        return studyModel.getId();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return "Name: " + studyModel.getName();
    }

    @Override
    public String getTitle(Locale locale) {
        return studyModel.getName();
    }
}