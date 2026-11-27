package com.tunicare.evaluation_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.project-id:evaluation-b2880}")
    private String projectId;

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @PostConstruct
    public void initializeFirebase() {
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("FirebaseApp est deja initialise.");
            return;
        }

        try {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setProjectId(projectId);

            InputStream serviceAccount = null;

            if (credentialsPath != null && !credentialsPath.trim().isEmpty()) {
                File file = new File(credentialsPath);
                if (file.exists()) {
                    serviceAccount = new FileInputStream(file);
                } else {
                    serviceAccount = getClass().getClassLoader().getResourceAsStream(credentialsPath);
                }
            }

            if (serviceAccount == null) {
                serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
            }
            if (serviceAccount == null) {
                serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            }

            if (serviceAccount != null) {
                optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
                logger.info("Firebase initialise avec le fichier de Service Account.");
            } else {
                try {
                    optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault());
                    logger.info("Firebase initialise avec les identifiants Google Application Default.");
                } catch (Exception e) {
                    logger.warn("Aucun fichier de credentials Firebase trouve. Fallback sur le projectId : {}", projectId);
                }
            }

            FirebaseApp.initializeApp(optionsBuilder.build());
            logger.info("FirebaseApp initialise avec succes pour le projet : {}", projectId);
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de Firebase : {}", e.getMessage(), e);
        }
    }

    @Bean
    public Firestore firestore() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                initializeFirebase();
            }
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            logger.error("Impossible d'obtenir l'instance Firestore : {}", e.getMessage());
            return null;
        }
    }
}
