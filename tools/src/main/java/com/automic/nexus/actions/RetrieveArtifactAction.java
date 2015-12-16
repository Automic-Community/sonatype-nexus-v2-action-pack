package com.automic.nexus.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.filter.GenericResponseFilter;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RetrieveArtifactAction extends AbstractHttpAction {

	private static final Logger LOGGER = LogManager
			.getLogger(RetrieveArtifactAction.class);

	private String groupID;
	private String artifactID;
	private String version;
	private String repository;
	private String packaging;
	private String classifier;
	private String extension;
	private String archiveFilePath;

	public RetrieveArtifactAction() {
		addOption("groupid", true, "Group ID of the artifact");
		addOption("artifactid", true, "Artifact ID");
		addOption("version", true, "Version of the artifact");
		addOption("repository", true,
				"Repository in which the artifact is located");
		addOption("packaging", false, "Packaging type of artifact");
		addOption("classifier", false, "Classifier of the artifact");
		addOption("extension", false, "Extension of the artifact");
		addOption("archivefilepath", true,
				"Target file path to save the artifact");
	}

	private void prepareInputParameters() throws AutomicException {
		try {
			archiveFilePath = getOptionValue("archivefilepath");
			NexusValidator.checkNotEmpty(archiveFilePath,
					"Target archive file path");
			NexusValidator.checkFileDirectoryExists(new File(archiveFilePath),
					"Target archive file path");

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
		} catch (AutomicException e) {
			LOGGER.error(e);
			throw e;
		}
	}

	@Override
	protected void executeSpecific() throws AutomicException {
		try {
			prepareInputParameters();
			initializeClient();
			client.addFilter(new GenericResponseFilter());
			if (!isAnonymous) {
				client.addFilter(new HTTPBasicAuthFilter(username, password));
			}
			ClientResponse response = null;

			WebResource webResource = client.resource(baseUrl).path("service")
					.path("local").path("artifact").path("maven")
					.path("content").queryParam("g", groupID)
					.queryParam("v", version).queryParam("r", repository)
					.queryParam("a", artifactID);

			if (CommonUtil.checkNotEmpty(packaging)) {
				webResource = webResource.queryParam("p", packaging);
			}
			if (CommonUtil.checkNotEmpty(classifier)) {
				webResource = webResource.queryParam("c", classifier);
			}
			if (CommonUtil.checkNotEmpty(extension)) {
				webResource = webResource.queryParam("e", extension);
			}
			LOGGER.info("Calling url " + webResource.getURI());

			response = webResource.get(ClientResponse.class);
			prepareOutput(response);
		} finally {
			if (client != null) {
				client.destroy();
			}
		}

	}

	private void prepareOutput(ClientResponse response) throws AutomicException {
		Path storedLocation = null;
		try (InputStream is = response.getEntityInputStream()) {
			storedLocation = Paths.get(archiveFilePath.toString());
			Files.copy(is, storedLocation);
			ConsoleWriter.writeln("UC4RB_NXS_ARCHIVE_PATH ::= "
					+ storedLocation);
		} catch (IOException e) {
			LOGGER.error(
					"Error while creating archive file from input stream ", e);
			throw new AutomicException(String.format(
					ExceptionConstants.UNABLE_TO_WRITEFILE, storedLocation));
		}

	}
	/**
     * Method to get Logger instance.
     *
     */
	protected Logger getLogger() {
		return LOGGER;
	}

}
