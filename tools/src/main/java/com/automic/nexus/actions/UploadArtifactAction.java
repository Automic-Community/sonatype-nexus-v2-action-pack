package com.automic.nexus.actions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class UploadArtifactAction extends AbstractHttpAction{
	
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
		addOption("groupid", true, "");
		addOption("artifactid", true, "");
		addOption("version", true, "");
		addOption("repository", true, "");
		addOption("packaging", false, "");
		addOption("classifier", false, "");
		addOption("extension", false, "");
		addOption("filepath", true, "");
	}
	
	private void prepareInputParameters() throws AutomicException {
		String temp = getOptionValue("filepath");
		filePath = new File(getOptionValue("filepath"));
		NexusValidator.checkFilePathNotEmpty(temp);
		NexusValidator.checkFileExists(filePath);
		
		groupID = getOptionValue("groupid");
		NexusValidator.checkNotEmpty(groupID, "Group ID");
		
		artifactID = getOptionValue("artifactid");
		NexusValidator.checkNotEmpty(artifactID, "Artifact ID");
		
		version = getOptionValue("version");
		NexusValidator.checkNotEmpty(version, "Version");
		
		repository = getOptionValue("repository");
		NexusValidator.checkNotEmpty(repository, "Repository");
		
		packaging = getOptionValue("packaging");
		
		classifier = getOptionValue("classifier");
		
		extension = getOptionValue("extension");
	}
	
	@Override
	protected void executeSpecific() throws AutomicException {
		prepareInputParameters();
		
		ClientResponse response = null;
		
		WebResource webResource = client.resource(baseUrl).path("service").path("local").path("artifact").path("maven").path("content");

        FileDataBodyPart fp = new FileDataBodyPart("file", filePath, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        
        FormDataMultiPart part = new FormDataMultiPart();
        part.field("g", groupID).field("v", version).field("r", repository).field("a", artifactID)
        .field("p", packaging).field("c", classifier).field("e", extension).bodyPart(fp);
        
        LOGGER.info("Calling url " + webResource.getURI());
        response = webResource.type(part.getMediaType()).post(ClientResponse.class,part);
	}

}
