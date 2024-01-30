package com.giaynhap.quanlynhac.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;


public class FirebaseConfig {

    static FirebaseApp createFireBaseApp(String pathToJson,String databaseUrl) throws IOException {
        System.out.println("firebase config");
        FileInputStream serviceAccount =
                new FileInputStream(pathToJson);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .build();

        return  FirebaseApp.initializeApp(options);
    }

}
