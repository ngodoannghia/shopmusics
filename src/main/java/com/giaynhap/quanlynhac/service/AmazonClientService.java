package com.giaynhap.quanlynhac.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.clouddirectory.model.UpdateObjectAttributesRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.giaynhap.quanlynhac.config.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

@Service
public class AmazonClientService {
    private AmazonS3 s3client;


    @Value("${amazon.accessKey}")
    private String accessKey;
    @Value("${amazon.secretKey}")
    private String secretKey;
    @Value("${amazon.music}")
    public String music;
    @Value("${amazon.photo}")
    public String photo;
    @Value("${amazon.musicDemo}")
    public String musicDemo;
    @Value("${amazon.musicStream}")
    public String musicStream;

    @Value("${amazon.bucket}")
    public String bucket;
    @Value("${amazon.region}")
    public String region;

    @Value("${amazon.enpoint}")
    public String enpoint;
    @PostConstruct
    private void initializeAmazon() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials, clientConfig);
        this.s3client.setEndpoint(enpoint);

        createBuket(bucket);

    }

    private void createBuket(String name){
        if (this.s3client.doesBucketExist(name)){
            return;
        }
        this.s3client.createBucket(name);
    }

    public void uploadFileToRemote(String folder,String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucket, folder+"/"+fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public void uploadFileToRemotePrivate(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public void deleteFileRemote(String fileName){
        s3client.deleteObject(bucket,fileName);
    }
    public void copyFileRemote(String folder,String fileName, String toFolder, String toFile){
        s3client.copyObject(bucket,fileName,folder+"/"+fileName,toFolder+"/"+toFile);
    }
    public URL getResourceURL(String folder, String file){
       return  s3client.getUrl(bucket,folder+"/"+file);
    }
    public URL getResourceURL(String file){
        return  s3client.getUrl(bucket,file);
    }
    public S3ObjectInputStream getMusic(String file,Long start, Long end){
        GetObjectRequest obj = new GetObjectRequest(bucket,file).withRange(start,end);
        return s3client.getObject(obj).getObjectContent();
    }
    public S3ObjectInputStream getMusic(String file){
        GetObjectRequest obj = new GetObjectRequest(bucket,file);
        return s3client.getObject(obj).getObjectContent();
    }
    public  boolean fileExist(String file){
       return  s3client.doesObjectExist(bucket,file);
    }
    public Long fileSize(String file){
        return  s3client.getObjectMetadata(bucket, file).getContentLength();
    }


    public void changeRoles(String file){
        s3client.setObjectAcl(bucket, file,CannedAccessControlList.PublicRead);
    }
}
