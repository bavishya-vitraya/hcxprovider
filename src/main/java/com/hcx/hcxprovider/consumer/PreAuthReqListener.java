package com.hcx.hcxprovider.consumer;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import com.nimbusds.jose.JOSEException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.swasth.jose.jwe.JweRequest;
import org.swasth.jose.jwe.key.PrivateKeyLoader;
import org.swasth.jose.jwe.key.PublicKeyLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PreAuthReqListener {
    @Autowired
    PreAuthRequestRepo preAuthRequestRepo;

    Map<String,Object> headers = new HashMap<>();
    Map<String,Object> payload = new HashMap<>();
    Map<String, String> encryptedObject = new HashMap<>();

    @RabbitListener(queues = "${queue.name}")
    public void recievedMessage(PreAuthReqDTO preAuthReqDTO) throws CertificateException, IOException, JOSEException, NoSuchAlgorithmException, InvalidKeySpecException, ParseException {
        System.out.println("Recieved Message From RabbitMQ: " + preAuthReqDTO);
        // Retrieving the actual request stored in DB using reference from queue.
        PreAuthRequest preAuthRequest = preAuthRequestRepo.findPreAuthRequestById(preAuthReqDTO.getReqId());

        //setting header and payload contents
        headers.put("content-type", MediaType.APPLICATION_JSON);
        payload.put(preAuthRequest.getId(),preAuthRequest.getPreAuthReq());

        // public key retrieval
        File file = new File("F:\\Vitraya\\HCX\\vitraya-mock-provider-public-cert.pem");
        FileReader fileReader = new FileReader(file);
        RSAPublicKey rsaPublicKey = PublicKeyLoader.loadPublicKeyFromX509Certificate(fileReader);

        //private key retrieval
        RSAPrivateKey rsaPrivateKey = PrivateKeyLoader.loadRSAPrivateKeyFromPem(new File("F:\\Vitraya\\HCX\\vitraya-mock-provider-private-key.pem"));

        //encryption logic
        JweRequest jweRequest = new JweRequest(headers, payload);
        jweRequest.encryptRequest(rsaPublicKey);
        encryptedObject = jweRequest.getEncryptedObject();
        System.out.println("Encrypted Object: " + encryptedObject.toString());

        //Decryption logic
        JweRequest decryptJweRequest = new JweRequest(encryptedObject);
        decryptJweRequest.decryptRequest(rsaPrivateKey);
        Map<String, Object> retrievedHeader = decryptJweRequest.getHeaders();
        Map<String, Object> retrievedPayload = decryptJweRequest.getPayload();
        System.out.println("Decrypted Object: " + retrievedHeader + "\n" + retrievedPayload);

        //RestTemplate restTemplate = new RestTemplate();
        //String response = restTemplate.postForObject("http://localhost:8401/testReq", encryptedObject, String.class);
        //System.out.println(preAuthRequest.getId() + preAuthRequest.getHospital_id() + preAuthRequest.getPreAuthReq());
        //System.out.println(preAuthRequest + response);
    }
}
