/**
 * 
 */
package ca.bc.gov.ag.courts.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.graph.core.models.IProgressCallback;
import com.microsoft.graph.core.models.UploadResult;
import com.microsoft.graph.core.tasks.LargeFileUploadTask;
import com.microsoft.graph.drives.item.items.item.createuploadsession.CreateUploadSessionPostRequestBody;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import ca.bc.gov.ag.courts.api.model.TestResponse;


/**
 * @author 176899
 * 
 * https://learn.microsoft.com/en-us/graph/sdks/large-file-upload?tabs=java#upload-large-file-to-onedrive
 * https://learn.microsoft.com/en-us/graph/sdks/create-client?tabs=java
 * https://stackoverflow.com/questions/45609432/how-do-i-resolve-the-error-aadsts7000218-the-request-body-must-contain-the-foll
 * 
 */
@Service
public class MSGraphServiceImpl implements MSGraphService {

	@Override
	public TestResponse UploadFile(String clientId, String tenantId, String[] scopes, File file) {

		TestResponse response = new TestResponse();
		response.setResult("Success");

		try {

			final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder().clientId(clientId)
					.tenantId(tenantId).challengeConsumer(challenge -> {
						// Display challenge to the user
						System.out.println(challenge.getMessage());
					}).build();

			if (null == scopes || null == credential) {
				throw new Exception("Unexpected error");
			}

			GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);

			InputStream fileStream = new FileInputStream(file);
			long streamSize = file.length();

			// Set body of the upload session request
			CreateUploadSessionPostRequestBody uploadSessionRequest = new CreateUploadSessionPostRequestBody();

			Map<String, Object> addDataMap = new HashMap<String, Object>();
			addDataMap.put("@microsoft.graph.conflictBehavior", "replace");
			uploadSessionRequest.setAdditionalData(addDataMap);

			String itemPath = "uploadedFromApi.txt"; // name of the file that will be placed on OneDrive. 

			// Create an upload session
			// ItemPath does not need to be a path to an existing item
			String myDriveId = graphClient.me().drive().get().getId();

			UploadSession uploadSession = graphClient.drives().byDriveId(myDriveId).items()
					.byDriveItemId("root:/" + itemPath + ":").createUploadSession().post(uploadSessionRequest);

			// Create the upload task
			int maxSliceSize = 320 * 10;
			LargeFileUploadTask<DriveItem> largeFileUploadTask = new LargeFileUploadTask<>(
					graphClient.getRequestAdapter(), uploadSession, fileStream, streamSize, maxSliceSize,
					DriveItem::createFromDiscriminatorValue);

			int maxAttempts = 5;

			// Create a callback used by the upload provider
			IProgressCallback callback = (current, max) -> System.out
					.println(String.format("Uploaded %d bytes of %d total bytes", current, max));

			// Do the upload
			UploadResult<DriveItem> uploadResult = largeFileUploadTask.upload(maxAttempts, callback);
			if (uploadResult.isUploadSuccessful()) {
				System.out.println("Upload complete");
				System.out.println("Item ID: " + uploadResult.itemResponse.getId());
			} else {
				response.setResult("Failed");
				response.setError("Upload Failed");
			}

		} catch (Exception ex) {
			response.setResult("Failed");
			response.setError(ex.getMessage());
		}

		return response;

	}

}
