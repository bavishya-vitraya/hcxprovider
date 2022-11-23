package com.hcx.hcxprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.hcx.hcxprovider.dto.PreAuthVhiResponse;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.Adjudication;
import org.hl7.fhir.r4.model.codesystems.ClaimType;
import org.hl7.fhir.r4.model.codesystems.ProcessPriority;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class HcxproviderApplication {

	public static Map<String, Object> setPayorConfig() throws IOException {
		Map<String, Object> config = new HashMap<>();
		File file = new File("C:\\Users\\flora\\OneDrive\\Documents\\keys\\vitraya-mock-payor-private-key.pem");
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
		PreAuthVhiResponse vhiResponse = new PreAuthVhiResponse();
		// claim response will come from payor connector apis - hard coded for now
		vhiResponse.setClaimNumber("CIR/2023/161200/1019485");
		vhiResponse.setClaimStatus(PreAuthVhiResponse.AdjudicationClaimStatus.APPROVED);
		vhiResponse.setClaimStatusInString("Approved");
		vhiResponse.setQuery("SUB-LIMIT APPLICABLE.\\n\\nPlease send us indoor case sheets, investigation reports, OT notes, post OP X-Ray images, implant invoices if applicable, discharge summary,  final bill with break up and other related documents.");
		vhiResponse.setApprovedAmount(BigDecimal.valueOf(20000));

		Patient patient = new Patient();// should fetch from claim request
		patient.setId("Patient/1");

		Organization organization = new Organization(); // should fetch from claim request
		organization.setId("organization/1");
		organization.setName("Test-HOS01");

		Claim claimRequest = new Claim(); // should fetch from claim request
		claimRequest.setId("Claim/1");
		claimRequest.setUse(Claim.Use.PREAUTHORIZATION);
		claimRequest.setId("Claim/1");
		claimRequest.setCreated(new Date());
		claimRequest.setStatus(Claim.ClaimStatus.ACTIVE);
		claimRequest.setType(new CodeableConcept(new Coding().setCode(ClaimType.INSTITUTIONAL.toCode()).setSystem("http://terminology.hl7.org/CodeSystem/claim-type")));
		claimRequest.setPriority(new CodeableConcept(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/processpriority").setCode(ProcessPriority.NORMAL.toCode())));
		claimRequest.setPatient(new Reference(patient.getId()));
		claimRequest.setProvider(new Reference(organization.getId()));
		claimRequest.addInsurance().setSequence(1).setFocal(true).setCoverage(new Reference("Coverage/1"));


		ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.setId("ClaimResponse/1");
		claimResponse.addIdentifier().setValue(vhiResponse.getClaimNumber());
		claimResponse.setPreAuthRef(vhiResponse.getClaimNumber());
		claimResponse.setOutcome(ClaimResponse.RemittanceOutcome.COMPLETE); // no approved enum provided
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


		Composition composition= new Composition();
		composition.setId("composition/" + UUID.randomUUID().toString());
		composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.getType().addCoding().setCode("HCXClaimResponse").setSystem("https://hcx.org/document-types").setDisplay("Claim Response");
		composition.setDate(new Date());
		composition.addAuthor().setReference("Organization/1");
		composition.setTitle("Claim Response");
		composition.addSection().addEntry().setReference("ClaimResponse/1");

		FhirContext fhirctx = FhirContext.forR4();
		Bundle bundle = new Bundle();
		bundle.setId(UUID.randomUUID().toString());
		bundle.setType(Bundle.BundleType.DOCUMENT);
		bundle.getIdentifier().setSystem("https://www.tmh.in/bundle").setValue(bundle.getId());
		bundle.setTimestamp(new Date());
		bundle.addEntry().setFullUrl(composition.getId()).setResource(composition);
		bundle.addEntry().setFullUrl(claimRequest.getId()).setResource(claimRequest);
		bundle.addEntry().setFullUrl(patient.getId()).setResource(patient);
		bundle.addEntry().setFullUrl(organization.getId()).setResource(organization);
		bundle.addEntry().setFullUrl(claimResponse.getId()).setResource(claimResponse);

		IParser p = fhirctx.newJsonParser().setPrettyPrint(true);
		String messageString = p.encodeResourceToString(bundle);
		System.out.println("here is the json " + messageString);

		HCXIntegrator.init(setPayorConfig());
		HCXOutgoingRequest hcxOutgoingRequest = new HCXOutgoingRequest();
		Map<String,Object> output = new HashMap<>();
		Operations operation = Operations.PRE_AUTH_ON_SUBMIT;
		File actionJweFile = new ClassPathResource("input/jweResponse").getFile();
		String actionJwe = FileUtils.readFileToString(actionJweFile);
		String status = "response.partial";
		Boolean res = hcxOutgoingRequest.generate(messageString,operation,actionJwe,status,output);
		System.out.println("{}"+res+output);
	}
	public static void main(String[] args) throws Exception {

		SpringApplication.run(HcxproviderApplication.class, args);
		getJWEResponsePayload();
	}

}
