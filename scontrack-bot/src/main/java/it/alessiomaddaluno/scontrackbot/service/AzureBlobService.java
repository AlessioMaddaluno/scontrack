package it.alessiomaddaluno.scontrackbot.service;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Service
public class AzureBlobService {

    @Value("${azure.blob-storage.connection}")
    private String AZURE_STORAGE_CONNECTION;

    private BlobContainerClient getClient(){
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(AZURE_STORAGE_CONNECTION).buildClient();
        String AZURE_BLOB_STORAGE_CONTAINER = "scontrack-images";
        return blobServiceClient.getBlobContainerClient(AZURE_BLOB_STORAGE_CONTAINER);
    }

    public void saveBlob(String blobName, String imageUrl) throws IOException {
        BlobContainerClient blobContainerClient = getClient();
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        InputStream imageFile = this.downloadFile(imageUrl);
        blobClient.upload(imageFile);
    }

    public void deleteBlob(String blobName){
        BlobContainerClient blobContainerClient = getClient();
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.deleteIfExists();
    }

    public InputStream downloadFile(String url) throws IOException {
        if(!url.startsWith("http")){
           url = "https://scontracksa.blob.core.windows.net/scontrack-images/"+url;
        }
        URL fileUrl = new URL(url);
        try (InputStream inputStream = fileUrl.openStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

}
