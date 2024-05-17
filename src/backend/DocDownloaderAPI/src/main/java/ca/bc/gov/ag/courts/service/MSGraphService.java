/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;



/**
 * MS Graph Service Interface
 * 
 * @author 176899
 *
 */
public interface MSGraphService {
	
	public CompletableFuture<String> createUploadSession(String accessToken, String fileFolder, String fileName) throws Exception;
	public CompletableFuture<JSONObject> uploadChunk(String uploadUrl, int count, long fileSize, byte[] chunk, int fragSize, int chunkSize) throws Exception;

}
