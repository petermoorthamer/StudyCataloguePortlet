package com.jnj.honeur.portlet;

import com.jnj.honeur.constants.StudyCataloguePortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + StudyCataloguePortletKeys.StudyCatalogue,
                "mvc.command.name=/study-details"
        },
        service = MVCRenderCommand.class
)
public class StudyDetailsMVCRenderCommand implements MVCRenderCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyDetailsMVCRenderCommand.class);

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
        LOGGER.info("StudyDetailsMVCRenderCommand.render");
        return "/study-details.jsp";
    }

}
