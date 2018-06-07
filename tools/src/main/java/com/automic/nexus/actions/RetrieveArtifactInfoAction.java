package com.automic.nexus.actions;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This action is used to retrieve an artifact informations from Nexus repository with the given parameters.
 * 
 */
public class RetrieveArtifactInfoAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(RetrieveArtifactInfoAction.class);
    
    private static final String SPLITTER = "=::=";
    private static final String SLASH = "/";
    private static final String CONTENT_REPOSITORIES = "content/repositories";

    private String groupID;
    private String artifactID;
    private String version;
    private String repository;
    private String archiveFilePath;
    private String downloadChecksum;

    public RetrieveArtifactInfoAction() {
        addOption("groupid", true, "Group ID of the artifact");
        addOption("artifactid", true, "Artifact ID");
        addOption("version", true, "Version of the artifact");
        addOption("repository", true, "Repository in which the artifact is located");
        addOption("archivefilepath", true, "Target file path to save the artifact");
        addOption("downloadchecksum", true, "Download and record checksum of downloaded file");
    }

    private void prepareInputParameters() throws AutomicException {
        try {
        	archiveFilePath = getOptionValue("archivefilepath");
            NexusValidator.checkNotEmpty(archiveFilePath, "Folder to save in");
            NexusValidator.checkDirectoryExists(new File(archiveFilePath), "Folder to save in");
        	
            groupID = getOptionValue("groupid");
            NexusValidator.checkNotEmpty(groupID, "Group ID");

            artifactID = getOptionValue("artifactid");
            NexusValidator.checkNotEmpty(artifactID, "Artifact ID");

            version = getOptionValue("version");
            NexusValidator.checkNotEmpty(version, "Version");

            repository = getOptionValue("repository");
            NexusValidator.checkNotEmpty(repository, "Repository");
            
            downloadChecksum = getOptionValue("downloadchecksum");
            NexusValidator.checkNotEmpty(downloadChecksum, "Download Checksum");
        } catch (AutomicException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputParameters();
        WebResource webResource = getClient();
        ClientResponse response = null;

        webResource = webResource.path("service").path("local").path("artifact").path("maven").path("resolve")
                .queryParam("g", groupID).queryParam("v", version).queryParam("r", repository)
                .queryParam("a", artifactID);
        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.get(ClientResponse.class);
        prepareOutput(response);

    }

    private void prepareOutput(ClientResponse response) throws AutomicException {
        if (response.getStatus() != 200) {
 		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
 		}
        String output = response.getEntity(String.class);
        try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResponseData.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ResponseData responseData = (ResponseData) jaxbUnmarshaller.unmarshal(new StringReader(output));
			NexusArtifactInfo artifactInfo = responseData.getData();
			String log = artifactInfo.getArtifactFileName() + SPLITTER + buildFullArtifactPath(artifactInfo);
			if ("YES".equalsIgnoreCase(downloadChecksum)) {
				log += SPLITTER + artifactInfo.getSha1Checksum();
				buildChecksumFile(artifactInfo);
			}
			ConsoleWriter.writeln(log);
		} catch (JAXBException e) {
			LOGGER.error("Fail to parse response from Nexus. ", e);
		} catch (IOException e) {
			LOGGER.error("Fail to create/write artifact checksum file. ", e);
		}
    }

    /**
     * Method to get Logger instance.
     * 
     */
    protected Logger getLogger() {
        return LOGGER;
    }
    
    private String buildFullArtifactPath(NexusArtifactInfo data) {
    	return baseUrl + SLASH + CONTENT_REPOSITORIES + SLASH + repository + (data != null ? data.getRepositoryPath() : "");
    }
    
    private void buildChecksumFile(NexusArtifactInfo artifactInfo) throws IOException {
    	File file = new File(archiveFilePath + SLASH + artifactInfo.getChecksumFileName());
    	Path path = Files.createFile(file.toPath());
    	FileUtils.writeStringToFile(path.toFile(), artifactInfo.getChecksum());
    }
    
    @XmlRootElement(name = "artifact-resolution")
    private static class ResponseData {
    	private NexusArtifactInfo data;

		public NexusArtifactInfo getData() {
			return data;
		}

		@XmlElement
		public void setData(NexusArtifactInfo data) {
			this.data = data;
		}
    }
    
    private static class NexusArtifactInfo {
    	private static final String SHA1 = "sha1";
    	private static final String DOT = ".";
    	
    	private String checksum;
    	private String repositoryPath;
		
		public String getSha1Checksum() {
			return SHA1 + ":" + checksum;
		}
		
		public String getChecksum() {
			return checksum;
		}
		
		public String getChecksumFileName() {
			return getArtifactFileName() + DOT + SHA1;
		}

		@XmlElement(name = "sha1")
		public void setChecksum(String checksum) {
			this.checksum = checksum;
		}
		
		public String getRepositoryPath() {
			return repositoryPath;
		}
		
		@XmlElement
		public void setRepositoryPath(String repositoryPath) {
			this.repositoryPath = repositoryPath;
		}
		
		public String getArtifactFileName() {
			String[] paths = repositoryPath.split(SLASH);
			return paths[paths.length - 1];
		}
    }
}
