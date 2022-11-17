package com.hcx.hcxprovider;

import com.hcx.hcxprovider.dto.preAuthVhiResponse;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.Adjudication;
import org.hl7.fhir.r4.model.codesystems.ClaimType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class HcxproviderApplication {

	public static Map<String, Object> setPayorConfig() throws IOException {
		Map<String, Object> config = new HashMap<>();
		File file = new ClassPathResource("keys/vitraya-mock-payor-private-key.pem").getFile();
		String privateKey= FileUtils.readFileToString(file);
		config.put("protocolBasePath", "http://staging-hcx.swasth.app/api/v0.7");
		config.put("authBasePath","http://a9dd63de91ee94d59847a1225da8b111-273954130.ap-south-1.elb.amazonaws.com:8080/auth/realms/swasth-health-claim-exchange/protocol/openid-connect/token");
		config.put("participantCode","1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
		config.put("username", "vitrayahcxpayor1@vitrayatech.com");
		config.put("password","BkYJHwm64EEn8B8");
		config.put("encryptionPrivateKey", privateKey);
		config.put("igUrl", "https://ig.hcxprotocol.io/v0.7");
		return config;
	}
	public static void getJWEResponsePayload() throws Exception {
		HCXIntegrator.init(setPayorConfig());
		File payloadFile = new ClassPathResource("input/claimResponse.json").getFile();
		String payload = FileUtils.readFileToString(payloadFile);
		preAuthVhiResponse vhiResponse = new preAuthVhiResponse();

		Patient patient = new Patient();
		patient.setId("Patient/1");

		Organization organization = new Organization();
		organization.setId("organization/1");

		Claim claimRequest = new Claim();
		claimRequest.setId("Claim/1");

		ClaimResponse claimResponse = new ClaimResponse();
		claimResponse.addIdentifier().setValue(vhiResponse.getClaimNumber());
		claimResponse.setPreAuthRef(vhiResponse.getClaimNumber());
		claimResponse.setOutcome(ClaimResponse.RemittanceOutcome.valueOf(vhiResponse.getClaimStatus().getStatus()));
		claimResponse.setDisposition(vhiResponse.getClaimStatusInString());
		claimResponse.addProcessNote().setText(vhiResponse.getQuery());
		claimResponse.addTotal().setAmount(new Money().setCurrency("INR").setValue(vhiResponse.getApprovedAmount())).setCategory(new CodeableConcept(new Coding().setCode(Adjudication.ELIGIBLE.toCode()).setSystem("http://terminology.hl7.org/CodeSystem/adjudication")));
		claimResponse.setStatus(ClaimResponse.ClaimResponseStatus.ACTIVE);
		claimResponse.setType(new CodeableConcept(new Coding().setCode(ClaimType.INSTITUTIONAL.toCode()).setSystem("http://terminology.hl7.org/CodeSystem/claim-type")));
		claimResponse.setUse(ClaimResponse.Use.PREAUTHORIZATION);
		claimResponse.setCreated(new Date());
		claimResponse.setPatient(new Reference(patient.getId()));
		claimResponse.setRequestor(new Reference(organization.getId()));
		claimResponse.setInsurer(new Reference(organization.getId()));
		claimResponse.setRequest(new Reference(claimRequest.getId()));


		HCXOutgoingRequest hcxOutgoingRequest = new HCXOutgoingRequest();
		Map<String,Object> output = new HashMap<>();
		Operations operation = Operations.PRE_AUTH_ON_SUBMIT;
		File actionJweFile = new ClassPathResource("input/jweResponse").getFile();
		String actionJwe = FileUtils.readFileToString(actionJweFile);
		String status = "response.partial";
		Boolean res = hcxOutgoingRequest.generate(payload,operation,actionJwe,status,output);
		System.out.println("{}"+res+output);
	}
	public static void main(String[] args) throws Exception {

		SpringApplication.run(HcxproviderApplication.class, args);
		getJWEResponsePayload();
	}

}
