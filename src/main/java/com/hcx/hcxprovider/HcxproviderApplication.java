package com.hcx.hcxprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hcx.hcxprovider.dto.*;
import io.hcxprotocol.impl.HCXIncomingRequest;
import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.JSONUtils;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.codesystems.Adjudication;
import org.hl7.fhir.r4.model.codesystems.ClaimType;
import org.hl7.fhir.r4.model.codesystems.ProcessPriority;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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


	public static void buildVhiClaimRequest() throws Exception {
	  File requestfile = new ClassPathResource("input/jweResponse").getFile();
		String request = FileUtils.readFileToString(requestfile);
		Operations operation = Operations.PRE_AUTH_SUBMIT;
		HCXIntegrator.init(setPayorConfig());
		Map<String,Object> output = new HashMap<>();
		Map<String,Object> input = new HashMap<>();
		input.put("payload",request);
		HCXIncomingRequest hcxIncomingRequest = new HCXIncomingRequest();
		hcxIncomingRequest.process(JSONUtils.serialize(input),operation,output);
		log.info("Incoming Request: {}",output);
		String fhirPayload = (String) output.get("fhirPayload");

		PreAuthDetails preAuthDetails= new PreAuthDetails();
		FhirContext fhirctx = FhirContext.forR4();
		IParser parser = fhirctx.newJsonParser().setPrettyPrint(true);
		Bundle bundle = parser.parseResource(Bundle.class, fhirPayload);
		Claim claim;
		com.hcx.hcxprovider.dto.Claim vhiClaim= new com.hcx.hcxprovider.dto.Claim();
		for(Bundle.BundleEntryComponent entryComponent: bundle.getEntry()) {
			String resourceType = entryComponent.getResource().getResourceType().toString();
			log.info(String.valueOf(entryComponent.getResource().getResourceType()));
			if (resourceType.equalsIgnoreCase("Claim")) {
				 claim= (Claim) entryComponent.getResource();
				 List<Claim.SupportingInformationComponent> supportingInfoList=new ArrayList<>();
				supportingInfoList=claim.getSupportingInfo();

				preAuthDetails.setClaimFlowType(ClaimFlowType.valueOf(claim.getUse().toString()));
                vhiClaim.setCreatedDate(claim.getCreated());




				for(Claim.SupportingInformationComponent supportingInfo:supportingInfoList){
					List<Coding> codingList= supportingInfo.getCategory().getCoding();
					for(Coding coding:codingList){
						 if(coding.getDisplay().equalsIgnoreCase("attachment.json")){
							String encodedString= supportingInfo.getValueStringType().toString();
							 byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
							 String attachment = new String(decodedBytes);
							AttachmentDTO attachmentDTO =  new Gson().fromJson(attachment, new TypeToken<AttachmentDTO>() {
							}.getType());
							 vhiClaim.setId(attachmentDTO.getParentTableId());
							 vhiClaim.setDeleted(attachmentDTO.isDeleted());
							 vhiClaim.setUpdatedDate(attachmentDTO.getUpdatedDate());
							 vhiClaim.setState(attachmentDTO.getState());
							 vhiClaim.setStatus(attachmentDTO.getStatus());
							 vhiClaim.setAge(attachmentDTO.getAge());
                             vhiClaim.setProductCode(attachmentDTO.getProductCode());
							 vhiClaim.setMedicalEventId(attachmentDTO.getMedicalEventId());

							 preAuthDetails.setServiceTypeId(attachmentDTO.getServiceTypeId());


						 }
						 else if(coding.getDisplay().equalsIgnoreCase("PolicyInceptionDate")){
                           vhiClaim.setPolicyInceptionDate(supportingInfo.getTimingDateType().getValue());
						 }
					}
				}

			}
			else if(resourceType.equalsIgnoreCase("patient")){
                Patient patient= (Patient) entryComponent.getResource();
				vhiClaim.setHospitalPatientId(patient.getIdentifier().get(0).getValue());
				vhiClaim.setDob(patient.getBirthDate());
				vhiClaim.setGender(patient.getGenderElement().getValueAsString());
				vhiClaim.setPatientName(patient.getName().get(0).getNameAsSingleString());
				vhiClaim.setPolicyHolderName(patient.getName().get(0).getNameAsSingleString());
				List<ContactPoint> telecomList=patient.getTelecom();
				 for(ContactPoint contactPoint:telecomList){
					 if(contactPoint.getSystem()== ContactPoint.ContactPointSystem.PHONE){
						 vhiClaim.setPatient_mobile_no(contactPoint.getValue());
					 }
					 if(contactPoint.getSystem()== ContactPoint.ContactPointSystem.EMAIL){
						 vhiClaim.setPatient_email_id(contactPoint.getValue());
					 }

				 }
				 vhiClaim.setAttendent_mobile_no(patient.getContact().get(0).getTelecom().get(0).getValue());

			}
			else if(resourceType.equalsIgnoreCase("procedure")){
                Procedure procedure= (Procedure) entryComponent.getResource();
			}
			else if(resourceType.equalsIgnoreCase("organization")){
				Organization organization= new Organization();
				if(entryComponent.getFullUrl().contains("InsurerOrganization")){
					organization= (Organization) entryComponent.getResource();
					vhiClaim.setInsuranceAgencyId(Integer.valueOf(organization.getIdentifier().get(0).getValue()));
				}
				else{
					organization= (Organization) entryComponent.getResource();
					List<Identifier> identifierList= organization.getIdentifier();
					for(Identifier identifier:identifierList){
						List<Coding> codingList=identifier.getType().getCoding();
						for(Coding code:codingList){
							if(code.getCode()=="PRN"){
								vhiClaim.setHospitalId(Integer.valueOf(identifier.getValue()));
							}
						}
					}
					vhiClaim.setCityName(organization.getContact().get(0).getPurpose().getText());
				}

			}
			else if(resourceType.equalsIgnoreCase("Practitioner")){
                Practitioner practitioner= (Practitioner) entryComponent.getResource();
				List<Identifier> identifierList= practitioner.getIdentifier();
				for(Identifier identifier:identifierList){
					List<Coding> codingList=identifier.getType().getCoding();
					 for(Coding code:codingList){
						 if(code.getCode()=="PLAC"){
							 vhiClaim.setCreatorId(Long.valueOf(identifier.getValue()));
						 }
					 }
				}

			}
			else if(resourceType.equalsIgnoreCase("Procedure")){
                 Procedure procedure= (Procedure) entryComponent.getResource();
			}
			else if(resourceType.equalsIgnoreCase("Condition")){
                Condition condition= (Condition) entryComponent.getResource();
			}
			else if(resourceType.equalsIgnoreCase("Coverage")){
				Coverage coverage= (Coverage) entryComponent.getResource();
				vhiClaim.setMedicalCardId(coverage.getSubscriberId());
				vhiClaim.setPolicyNumber(coverage.getIdentifier().get(0).getValue());
				vhiClaim.setPolicyType(PolicyType.valueOf(coverage.getType().getText()));
				vhiClaim.setPolicyEndDate(coverage.getPeriod().getEnd());
				vhiClaim.setPolicyName(coverage.getClass_().get(0).getValue());
                vhiClaim.setPolicyStartDate(coverage.getPeriod().getStart());
			}

		}
		log.info("vhiclaim{}",vhiClaim);
	}
	public static void main(String[] args) throws Exception {

		SpringApplication.run(HcxproviderApplication.class, args);
		getJWEResponsePayload();
		//buildVhiClaimRequest();
	}

}
