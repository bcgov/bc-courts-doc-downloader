package ca.bc.gov.ag.courts.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ca.bc.gov.ag.courts.config.AppProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;

@Component
public class MinioS3Utils {

    private static final Logger logger = LoggerFactory.getLogger(MinioS3Utils.class);

    private MinioClient minioClient;
    
    //TODO - Need to find out what this is for the S3 storage. 
    private final long MAX_FILE_SIZE = 1073741824;
    
    private AppProperties props; 
    
    public MinioS3Utils (AppProperties props) {
    	this.props = props; 
    }
    
    @PostConstruct
	private void postConstruct() {
		
		// Connection to S3 Compatible Server AG server (user creds)
    	// Note that region is required in the constructor even though it's not required by the AG server. 
		minioClient = MinioClient.builder().endpoint(props.getS3AccessEndpoint())
				.credentials(props.getS3AccessKeyid(), props.getS3AccessSecretkey())
				.region("east")
				.build();
		
	}

    // Upload Files
    public void putObject(String bucketName, MultipartFile multipartFile, String filename, String fileType) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                        inputStream, -1, MAX_FILE_SIZE)
                        .contentType(fileType)
                        .build());
    }

    // Check for pre existing bucket name
    public boolean bucketExists(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean found =
                minioClient.bucketExists(
                        BucketExistsArgs.builder().
                                bucket(bucketName).
                                build());
        return found;
    }

    // Create bucket name
    public boolean makeBucket(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        } else {
            return false;
        }
    }

    // List all buckets
    public List<Bucket> listBuckets() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        
    	return minioClient.listBuckets();
    }

    // List all bucket names
    public List<String> listBucketNames() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {

        List<Bucket> bucketList = listBuckets();

        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }

        return bucketListName;
    }

    // List all objects from the specified bucket
    public Iterable<Result<Item>> listObjects(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    // Delete Bucket by its name from the specified bucket
    public boolean removeBucket(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);

            for (Result<Item> result : myObjects) {
                Item item = result.get();
                //  Delete failed when There are object files in bucket


                if (item.size() > 0) {
                    return false;
                }
            }

            //  Delete bucket when bucket is empty
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);

            if (!flag) {
                return true;
            }
        }
        return false;
    }

    // List all object names from the specified bucket
    public List<String> listObjectNames(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);

        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        } else {
            listObjectNames.add("Bucket does not exist");
        }

        return listObjectNames;
    }

    // Delete object from the specified bucket
    public boolean removeObject(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    // Get file path(url) for object name
    public String getObjectUrl(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        String url = null;

        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(2, TimeUnit.MINUTES)
                            .build());
        }
        
        return url;
    }

    // Get metadata of the object from the specified bucket
    public StatObjectResponse statObject(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
      
        boolean flag = bucketExists(bucketName);
       
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());

            return stat;
        }
        return null;
    }

    // Get a file object as a stream from the specified bucket
    public InputStream getObject(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
       
        boolean flag = bucketExists(bucketName);
        
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());

                return stream;
            }
        }
        return null;
    }

    // Get a file object as a stream from the specified bucket （ Breakpoint download )
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .offset(offset)
                                        .length(length)
                                        .build());

                return stream;
            }
        }
        return null;
    }

    // Delete multiple file objects from the specified bucket
    public boolean removeObject(String bucketName, List<String> objectNames) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
      
        boolean flag = bucketExists(bucketName);

        if (flag) {
            List<DeleteObject> objects = new LinkedList<>();
            for (int i = 0; i < objectNames.size(); i++) {
                objects.add(new DeleteObject(objectNames.get(i)));
            }
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                
                logger.error("Unable to remove list of bucket objects: " + error.message());

                return false;
            }
        }
        return true;
    }

    // Upload InputStream object to the specified bucket
    public boolean putObject(String bucketName, String objectName, InputStream inputStream, String contentType) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {

        boolean flag = bucketExists(bucketName);

        if (flag) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                            inputStream, -1, MAX_FILE_SIZE)
                            .contentType(contentType)
                            .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);

            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        
        return false;
    }
}
