package com.automic.nexus.actions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * This action is used to upload an artifact to Nexus repository with the given GAV parameters.
 * 
 * @author shrutinambiar
 *
 */
public class UploadArtifactAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(UploadArtifactAction.class);

    private String groupID;
    private String artifactID;
    private String version;
    private String repository;
    private String packaging;
    private String classifier;
    private String extension;
    private File filePath;

    public UploadArtifactAction() {
        addOption("groupid", true, "Group ID of the artifact");
        addOption("artifactid", true, "Artifact ID");
        addOption("version", true, "Version of the artifact");
        addOption("repository", true, "Repository in which the artifact is to be uploaded");
        addOption("packaging", false, "Packaging type of artifact");
        addOption("classifier", false, "Classifier of the artifact");
        addOption("extension", false, "Extension of the artifact");
        addOption("filepath", true, "Artifact location path with full name");
    }

    /**
     * Validate and initalize input.
     * 
     * @throws AutomicException
     */
    private void prepareInputParameters() throws AutomicException {
        filePath = new File(getOptionValue("filepath"));
        NexusValidator.checkFileExists(filePath, "Artifact File Path");

        groupID = getOptionValue("groupid");
        NexusValidator.checkNotEmpty(groupID, "Group ID");

        artifactID = getOptionValue("artifactid");
        NexusValidator.checkNotEmpty(artifactID, "Artifact ID");

        version = getOptionValue("version");
        NexusValidator.checkNotEmpty(version, "Version");

        repository = getOptionValue("repository");
        NexusValidator.checkNotEmpty(repository, "Repository");
        
        classifier = getOptionValue("classifier");
        packaging = getOptionValue("packaging");
        extension = getOptionValue("extension");
        boolean hasPackage = CommonUtil.checkNotEmpty(packaging);
        if(!hasPackage) {
            boolean hasExtension = CommonUtil.checkNotEmpty(extension);
            if(!hasExtension) {
                String msg = "At least Package or Extension should be provided ";
                LOGGER.error(msg);
                throw new AutomicException(msg);
            }
        }
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputParameters();
        WebResource webResource = getClient();
		webResource.path("service").path("local").path("artifact").path("maven")
                .path("content");

        FileDataBodyPart fp = new FileDataBodyPart("file", filePath, MediaType.APPLICATION_OCTET_STREAM_TYPE);

        FormDataMultiPart part = new FormDataMultiPart();
        part.field("g", groupID).field("v", version).field("r", repository).field("a", artifactID)
                .field("p", packaging).field("c", classifier).field("e", extension).bodyPart(fp);

        LOGGER.info("Calling url " + webResource.getURI());
        webResource.type(part.getMediaType()).post(ClientResponse.class, part);
    }

    /**
	 * Method to get Logger instance.
	 * 
	 */
	protected Logger getLogger() {
		return LOGGER;
	}
}
