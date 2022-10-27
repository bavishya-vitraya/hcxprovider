package com.hcx.hcxprovider.consumer;

import com.hcx.hcxprovider.dto.PreAuthReqDTO;
import com.hcx.hcxprovider.model.PreAuthRequest;
import com.hcx.hcxprovider.repository.PreAuthRequestRepo;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.utils.Operations;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void recievedMessage(PreAuthReqDTO preAuthReqDTO) throws Exception {
        System.out.println("Recieved Message From RabbitMQ: " + preAuthReqDTO);
        // Retrieving the actual request stored in DB using reference from queue.
        PreAuthRequest preAuthRequest = preAuthRequestRepo.findPreAuthRequestById(preAuthReqDTO.getReqId());

       /* //setting header and payload contents
        headers.put("content-type", MediaType.APPLICATION_JSON);
        payload.put(preAuthRequest.getId(),preAuthRequest.getPreAuthReq());

        // public key retrieval
        File file = new File("F:\\Vitraya\\HCX\\vitraya-mock-provider-public-cert.pem");
        FileReader fileReader = new FileReader(file);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) PublicKeyLoader.loadPublicKeyFromX509Certificate(fileReader);

        //private key retrieval
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) PrivateKeyLoader.loadRSAPrivateKeyFromPem(new File("F:\\Vitraya\\HCX\\vitraya-mock-provider-private-key.pem"));

        //encryption logic
        JweRequest jweRequest = new JweRequest(headers, payload);
        jweRequest.encryptRequest((java.security.interfaces.RSAPublicKey) rsaPublicKey);
        encryptedObject = jweRequest.getEncryptedObject();
        System.out.println("Encrypted Object: " + encryptedObject);

        //Decryption logic
        JweRequest decryptJweRequest = new JweRequest(encryptedObject);
        decryptJweRequest.decryptRequest((java.security.interfaces.RSAPrivateKey) rsaPrivateKey);
        Map<String, Object> retrievedHeader = decryptJweRequest.getHeaders();
        Map<String, Object> retrievedPayload = decryptJweRequest.getPayload();
        System.out.println("Decrypted Object: " + retrievedHeader + "\n" + retrievedPayload);

        //RestTemplate restTemplate = new RestTemplate();
        //String response = restTemplate.postForObject("http://localhost:8401/testReq", encryptedObject, String.class);
        //System.out.println(preAuthRequest.getId() + preAuthRequest.getHospital_id() + preAuthRequest.getPreAuthReq());
        //System.out.println("Response from connector:"+response);*/
        String payload = "{\n  \"resourceType\": \"Bundle\",\n  \"id\": \"d4484cdd-1aae-4d21-a92e-8ef749d6d366\",\n  \"meta\": {\n    \"lastUpdated\": \"2022-02-08T21:49:55.458+05:30\"\n  },\n  \"identifier\": {\n    \"system\": \"https://www.tmh.in/bundle\",\n    \"value\": \"d4484cdd-1aae-4d21-a92e-8ef749d6d366\"\n  },\n  \"type\": \"document\",\n  \"timestamp\": \"2022-02-08T21:49:55.458+05:30\",\n  \"entry\": [\n    {\n      \"fullUrl\": \"Composition/42ff4a07-3e36-402f-a99e-29f16c0c9eee\",\n      \"resource\": {\n        \"resourceType\": \"Composition\",\n        \"id\": \"42ff4a07-3e36-402f-a99e-29f16c0c9eee\",\n        \"identifier\": {\n          \"system\": \"https://www.tmh.in/hcx-documents\",\n          \"value\": \"42ff4a07-3e36-402f-a99e-29f16c0c9eee\"\n        },\n        \"status\": \"final\",\n        \"type\": {\n          \"coding\": [\n            {\n              \"system\": \"https://www.hcx.org/document-type\",\n              \"code\": \"HcxPreauthRequest\",\n              \"display\": \"Preauth Request Doc\"\n            }\n          ]\n        },\n        \"subject\": {\n          \"reference\": \"Patient/RVH1003\"\n        },\n        \"date\": \"2022-02-08T21:49:55+05:30\",\n        \"author\": [\n          {\n            \"reference\": \"Organization/Tmh01\"\n          }\n        ],\n        \"title\": \"Preauth Request\",\n        \"section\": [\n          {\n            \"title\": \"# Preauth Request\",\n            \"code\": {\n              \"coding\": [\n                {\n                  \"system\": \"http://hl7.org/fhir/ValueSet/resource-types\",\n                  \"code\": \"PreauthRequest\",\n                  \"display\": \"Preauth Request\"\n                }\n              ]\n            },\n            \"entry\": [\n              {\n                \"reference\": \"PreauthRequest/dc82673b-8c71-48c2-8a17-16dcb3b035f6\"\n              }\n            ]\n          }\n        ]\n      }\n    },\n    {\n      \"fullUrl\": \"Organization/Tmh01\",\n      \"resource\": {\n        \"resourceType\": \"Organization\",\n        \"id\": \"Tmh01\",\n        \"identifier\": [\n          {\n            \"system\": \"http://abdm.gov.in/facilities\",\n            \"value\": \"HFR-ID-FOR-TMH\"\n          },\n          {\n            \"system\": \"http://irdai.gov.in/facilities\",\n            \"value\": \"IRDA-ID-FOR-TMH\"\n          }\n        ],\n        \"name\": \"Tata Memorial Hospital\",\n        \"alias\": [\n          \"TMH\",\n          \"TMC\"\n        ],\n        \"telecom\": [\n          {\n            \"system\": \"phone\",\n            \"value\": \"(+91) 022-2417-7000\"\n          }\n        ],\n        \"address\": [\n          {\n            \"line\": [\n              \"Dr Ernest Borges Rd, Parel East, Parel, Mumbai, Maharashtra 400012\"\n            ],\n            \"city\": \"Mumbai\",\n            \"state\": \"Maharashtra\",\n            \"postalCode\": \"400012\",\n            \"country\": \"INDIA\"\n          }\n        ],\n        \"endpoint\": [\n          {\n            \"reference\": \"https://www.tmc.gov.in/\",\n            \"display\": \"Website\"\n          }\n        ]\n      }\n    },\n    {\n      \"fullUrl\": \"Patient/RVH1003\",\n      \"resource\": {\n        \"resourceType\": \"Patient\",\n        \"id\": \"RVH1003\",\n        \"identifier\": [\n          {\n            \"type\": {\n              \"coding\": [\n                {\n                  \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n                  \"code\": \"SN\",\n                  \"display\": \"Subscriber Number\"\n                }\n              ]\n            },\n            \"system\": \"http://gicofIndia.com/beneficiaries\",\n            \"value\": \"BEN-101\"\n          },\n          {\n            \"system\": \"http://abdm.gov.in/patients\",\n            \"value\": \"hinapatel@abdm\"\n          } \n        ],\n        \"name\": [\n          {\n            \"text\": \"Hina Patel\"\n          }\n        ],\n        \"gender\": \"female\"\n      }\n    },\n    {\n      \"fullUrl\": \"PreauthRequest/dc82673b-8c71-48c2-8a17-16dcb3b035f6\",\n      \"resource\": {\n  \"resourceType\": \"Preauth\",\n  \"id\": \"1532676\",\n  \"meta\": {\n    \"versionId\": \"1\",\n    \"lastUpdated\": \"2020-10-07T03:26:09.060+00:00\",\n    \"source\": \"#BHZcRrcF4oS0JQ42\"\n  },\n  \"status\": \"active\",\n  \"type\": {\n    \"coding\": [ {\n      \"system\": \"http://terminology.hl7.org/CodeSystem/preauth-type\",\n      \"code\": \"institutional\"\n    } ]\n  },\n  \"use\": \"preauth\",\n  \"patient\": {\n    \"reference\": \"Patient/1531634\",\n    \"display\": \"Hina Patel\"\n  },\n  \"billablePeriod\": {\n    \"start\": \"1979-02-27T16:01:08-05:00\",\n    \"end\": \"1979-02-28T16:01:08-05:00\"\n  },\n  \"created\": \"1979-02-28T16:01:08-05:00\",\n  \"provider\": {\n    \"reference\": \"Organization/2\",\n    \"display\": \"PMJAY, MoHFW, Govt Of India\"\n  },\n  \"priority\": {\n    \"coding\": [ {\n      \"system\": \"http://terminology.hl7.org/CodeSystem/processpriority\",\n      \"code\": \"normal\"\n    } ]\n  },\n  \"procedure\": [ {\n    \"sequence\": 1,\n    \"procedureReference\": {\n      \"reference\": \"Procedure/1532663\"\n    }\n  }, {\n    \"sequence\": 2,\n    \"procedureReference\": {\n      \"reference\": \"Procedure/1532664\"\n    }\n  }, {\n    \"sequence\": 3,\n    \"procedureReference\": {\n      \"reference\": \"Procedure/1532665\"\n    }\n  } ],\n  \"insurance\": [ {\n    \"sequence\": 1,\n    \"focal\": true,\n    \"coverage\": {\n      \"display\": \"Cigna Health\"\n    }\n  } ],\n  \"item\": [ {\n    \"sequence\": 1,\n    \"productOrService\": {\n      \"coding\": [ {\n        \"system\": \"http://snomed.info/sct\",\n        \"code\": \"185347001\",\n        \"display\": \"Encounter for problem\"\n      } ],\n      \"text\": \"Encounter for problem\"\n    },\n    \"encounter\": [ {\n      \"reference\": \"Encounter/1532617\"\n    } ]\n  }, {\n    \"sequence\": 2,\n    \"procedureSequence\": [ 1 ],\n    \"productOrService\": {\n      \"coding\": [ {\n        \"system\": \"http://snomed.info/sct\",\n        \"code\": \"398171003\",\n        \"display\": \"Hearing examination (procedure)\"\n      } ],\n      \"text\": \"Hearing examination (procedure)\"\n    },\n    \"net\": {\n      \"value\": 516.65,\n      \"currency\": \"USD\"\n    }\n  }, {\n    \"sequence\": 3,\n    \"procedureSequence\": [ 2 ],\n    \"productOrService\": {\n      \"coding\": [ {\n        \"system\": \"http://snomed.info/sct\",\n        \"code\": \"703423002\",\n        \"display\": \"Combined chemotherapy and radiation therapy (procedure)\"\n      } ],\n      \"text\": \"Combined chemotherapy and radiation therapy (procedure)\"\n    },\n    \"net\": {\n      \"value\": 16924.49,\n      \"currency\": \"USD\"\n    }\n  }, {\n    \"sequence\": 4,\n    \"procedureSequence\": [ 3 ],\n    \"productOrService\": {\n      \"coding\": [ {\n        \"system\": \"http://snomed.info/sct\",\n        \"code\": \"16335031000119103\",\n        \"display\": \"High resolution computed tomography of chest without contrast (procedure)\"\n      } ],\n      \"text\": \"High resolution computed tomography of chest without contrast (procedure)\"\n    },\n    \"net\": {\n      \"value\": 516.65,\n      \"currency\": \"USD\"\n    }\n  } ],\n  \"total\": {\n    \"value\": 129.16,\n    \"currency\": \"USD\"\n  }\n}\n    },\n    {\n      \"fullUrl\": \"Organization/GICOFINDIA\",\n      \"resource\": {\n        \"resourceType\": \"Organization\",\n        \"id\": \"GICOFINDIA\",\n        \"identifier\": [\n          {\n            \"system\": \"http://irdai.gov.in/insurers\",\n            \"value\": \"112\"\n          }\n        ],\n        \"name\": \"General Insurance Corporation of India\"\n      }\n    },\n    {\n      \"fullUrl\": \"Coverage/dadde132-ad64-4d18-8c18-1d52d7e86abc\",\n      \"resource\": {\n        \"resourceType\": \"Coverage\",\n        \"id\": \"dadde132-ad64-4d18-8c18-1d52d7e86abc\",\n        \"identifier\": [\n          {\n            \"system\": \"https://www.gicofIndia.in/policies\",\n            \"value\": \"policy-RVH1003\"\n          }\n        ],\n        \"status\": \"active\",\n        \"subscriber\": {\n          \"reference\": \"Patient/RVH1003\"\n        },\n        \"subscriberId\": \"SN-RVH1003\",\n        \"beneficiary\": {\n          \"reference\": \"Patient/RVH1003\"\n        },\n        \"relationship\": {\n          \"coding\": [\n            {\n              \"system\": \"http://hl7.org/fhir/ValueSet/subscriber-relationship\",\n              \"code\": \"self\"\n            }\n          ]\n        },\n        \"payor\": [\n          {\n            \"reference\": \"Organization/GICOFINDIA\"\n          }\n        ]\n      }\n    }\n  ]\n}";
        Operations operation = Operations.PRE_AUTH_SUBMIT;
        String recipient_code = "1-434d79f6-aad8-48bc-b408-980a4dbd90e2";
        Map<String,Object> output = new HashMap<>();
        HCXOutgoingRequest hcxOutgoingRequest = new HCXOutgoingRequest();
        Boolean response = hcxOutgoingRequest.generate(payload,operation,recipient_code,output);
        System.out.println(operation+response.toString());
    }
}
