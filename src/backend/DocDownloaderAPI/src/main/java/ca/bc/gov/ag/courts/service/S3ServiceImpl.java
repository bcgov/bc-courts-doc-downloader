package ca.bc.gov.ag.courts.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.courts.Utils.MinioS3Utils;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Bucket;
import jakarta.annotation.PostConstruct;

/**
 * 
 * S3 Service Implementation using MinioS3 Utils class. 
 * 
 * @author 176899
 *
 */

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private MinioS3Utils minioS3Utils;
    
    public S3ServiceImpl (MinioS3Utils minioS3Utils) {
    	this.minioS3Utils = minioS3Utils; 
    }
    
    @PostConstruct
	public void init() throws UnknownHostException {
		logger.info("S3 Service started.");
	}

    @Override
    public boolean bucketExists(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to bucketExists, bucket name " + bucketName);

        return minioS3Utils.bucketExists(bucketName);
    }

    @Override
    public List<String> listBucketName() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
    	logger.debug("Heard a call to listBucketName");
        return minioS3Utils.listBucketNames();
    }

    @Override
    public List<Bucket> listBuckets() throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
    	logger.debug("Heard a call to listBuckets");
        return minioS3Utils.listBuckets();
    }

    @Override
    public List<String> listObjectNames(String bucketName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to listObjectNames, bucketName " + bucketName);

        return minioS3Utils.listObjectNames(bucketName);
    }
    
    @Override
    public boolean objectExists(String bucketName, String objectName)  {
    	logger.debug("Heard a call to objectExists, bucketName " + bucketName + ", objectName " + objectName);
    	
    	try {
    		minioS3Utils.statObject(bucketName, objectName);
    		return true; 
    	} catch (Exception ex) {
    		return false; 
    	}
    	
    }
    
    @Override
    public StatObjectResponse statObject(String bucketName, String objectName) {
    	logger.debug("Heard a call to statObject, bucketName " + bucketName + ", objectName " + objectName);
    	
    	try {
    		return minioS3Utils.statObject(bucketName, objectName); 
    	} catch (Exception ex) {
    		logger.error("Error at statObject. " + ex.getMessage());
    		return null; 
    	}
    }
    
    @Override
    public InputStream downloadObject(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to downloadObject, bucketName " + bucketName + ", objectName " + objectName);
        
        return minioS3Utils.getObject(bucketName,objectName);
    }

    @Override
    public boolean removeObject(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to removeObject, bucketName " + bucketName + ", objectName " + objectName);

        return minioS3Utils.removeObject(bucketName, objectName);
    }

    @Override
    public boolean removeListObject(String bucketName, List<String> objectNameList) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to removeListObject, bucketName " + bucketName + ", objectNameList " + objectNameList);

        return minioS3Utils.removeObject(bucketName,objectNameList);
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IllegalArgumentException, IOException {
    	logger.debug("Heard a call to getObjectUrl, bucketName " + bucketName + ", objectName " + objectName);

        return minioS3Utils.getObjectUrl(bucketName, objectName);
    }
}
