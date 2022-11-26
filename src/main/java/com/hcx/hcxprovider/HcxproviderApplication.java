package com.hcx.hcxprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hcx.hcxprovider.dto.*;
import com.hcx.hcxprovider.util.DateUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@SpringBootApplication
public class HcxproviderApplication {

	public static Map<String, Object> setPayorConfig() throws IOException {
		Map<String, Object> config = new HashMap<>();
		File file = new ClassPathResource("keys/vitraya-mock-payor-private-key.pem").getFile();;
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
		FileDTO file= new FileDTO();
		file.setName("GAL_PreauthInitialApprovalLetter_CLMG_2023_161200_1017744_1667392133684.pdf");
		file.setDocId("117833144");
		List<FileDTO> fileDTOList= new ArrayList<>();
		fileDTOList.add(file);

		// claim response will come from payor connector apis - hard coded for now
		vhiResponse.setClaimNumber("CIR/2023/161200/1019485");
		vhiResponse.setClaimStatus(PreAuthVhiResponse.AdjudicationClaimStatus.APPROVED);
		vhiResponse.setClaimStatusInString("Approved");
		vhiResponse.setQuery("SUB-LIMIT APPLICABLE.\\n\\nPlease send us indoor case sheets, investigation reports, OT notes, post OP X-Ray images, implant invoices if applicable, discharge summary,  final bill with break up and other related documents.");
		vhiResponse.setFiles(fileDTOList);
		vhiResponse.setApprovedAmount(BigDecimal.valueOf(20000));


		AttachmentResDTO attachmentResDTO = new AttachmentResDTO();
		attachmentResDTO.setQuery(vhiResponse.getQuery());
		attachmentResDTO.setFiles(vhiResponse.getFiles());

		String attachmentString = new Gson().toJson(attachmentResDTO);
		String encodedAttachement = Base64.getUrlEncoder().encodeToString(attachmentString.getBytes());

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
		claimResponse.addProcessNote().setType(Enumerations.NoteType.DISPLAY).setText(encodedAttachement);
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
		ClaimIllnessTreatmentDetails claimIllnessTreatmentDetails= new ClaimIllnessTreatmentDetails();
		ClaimAdmissionDetails 	claimAdmissionDetails= new 	ClaimAdmissionDetails();
		HospitalServiceType  hospitalServiceType= new  HospitalServiceType();
		com.hcx.hcxprovider.dto.Procedure vhiProcedure= new com.hcx.hcxprovider.dto.Procedure();
		ProcedureMethod procedureMethod= new ProcedureMethod();
		Illness illness= new Illness();
		for(Bundle.BundleEntryComponent entryComponent: bundle.getEntry()) {
			String resourceType = entryComponent.getResource().getResourceType().toString();
			log.info(String.valueOf(entryComponent.getResource().getResourceType()));
			if (resourceType.equalsIgnoreCase("Claim")) {
				 claim= (Claim) entryComponent.getResource();
				preAuthDetails.setClaimFlowType(ClaimFlowType.PRE_AUTH);
				vhiClaim.setCreatedDate(DateUtils.formatDate(claim.getCreated()));
				 List<Claim.ItemComponent> itemList = claim.getItem();
				 for(Claim.ItemComponent item:itemList){
					 List<Coding> productCodingList =item.getProductOrService().getCoding();
					 for(Coding coding: productCodingList ){
						if(coding.getDisplay().equalsIgnoreCase("Expense")){
							 hospitalServiceType.setRoomTariffPerDay(item.getUnitPrice().getValue());
						 }
					 }

				 }

				 List<Claim.SupportingInformationComponent> supportingInfoList=new ArrayList<>();
				 supportingInfoList=claim.getSupportingInfo();
				 for(Claim.SupportingInformationComponent supportingInfo:supportingInfoList){
					 List<Coding> codingList= supportingInfo.getCode().getCoding();
					 for(Coding coding:codingList){
						 if(coding.getCode().equalsIgnoreCase("ONS-6")){
							 claimAdmissionDetails.setIcuStay(supportingInfo.getValueBooleanType().booleanValue());
						 }
						 else if(coding.getCode().equalsIgnoreCase("ONS-1")){
							 claimAdmissionDetails.setAdmissionDate(DateUtils.formatDate(supportingInfo.getTimingDateType().getValue()));
						 }
						 else if(coding.getCode().equalsIgnoreCase("ONS-2")){
							 claimAdmissionDetails.setDischargeDate(DateUtils.formatDate(supportingInfo.getTimingDateType().getValue()));
						 }
						 else if(coding.getCode().equalsIgnoreCase("INF-1")){
							 String encodedString= supportingInfo.getValueStringType().toString();
							 byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
							 String attachment = new String(decodedBytes);
							 AttachmentDTO attachmentDTO =  new Gson().fromJson(attachment, new TypeToken<AttachmentDTO>() {
							 }.getType());
							 vhiClaim.setId(attachmentDTO.getParentTableId());
							 vhiClaim.setDeleted(attachmentDTO.isDeleted());
							 vhiClaim.setUpdatedDate(DateUtils.formatDate(attachmentDTO.getUpdatedDate()));
							 vhiClaim.setState(attachmentDTO.getState());
							 vhiClaim.setStatus(attachmentDTO.getStatus());
							 vhiClaim.setAge(attachmentDTO.getAge());
							 vhiClaim.setProductCode(attachmentDTO.getProductCode());
							 vhiClaim.setMedicalEventId(attachmentDTO.getMedicalEventId());
							 vhiClaim.setPolicyInceptionDate(attachmentDTO.getPolicyInceptionDate());

							 claimIllnessTreatmentDetails.setClaimId(attachmentDTO.getParentTableId());
							 claimIllnessTreatmentDetails.setChronicIllnessDetails(attachmentDTO.getChronicIllnessDetailsJSON().getChronicIllnessList().toString());
							 claimIllnessTreatmentDetails.setProcedureCorporateMappingId(attachmentDTO.getProcedureCorporateMappingId());
							 claimIllnessTreatmentDetails.setProcedureId(attachmentDTO.getProcedureId());
							 claimIllnessTreatmentDetails.setLeftImplant(attachmentDTO.getLeftImplant());
							 claimIllnessTreatmentDetails.setRightImplant(attachmentDTO.getRightImplant());
							 if(attachmentDTO.getChronicIllnessDetailsJSON().getChronicIllnessList()!=null) {
								 claimIllnessTreatmentDetails.setChronicIllnessDetailsJSON(attachmentDTO.getChronicIllnessDetailsJSON());
							 }

							 claimAdmissionDetails.setClaimId(attachmentDTO.getParentTableId());
							 claimAdmissionDetails.setHospitalServiceTypeId(attachmentDTO.getHospitalServiceTypeId());
							 claimAdmissionDetails.setStayDuration(attachmentDTO.getStayDuration());
							 claimAdmissionDetails.setPackageAmount(attachmentDTO.getPackageAmount());
							 claimAdmissionDetails.setCostEstimation(attachmentDTO.getCostEstimation());
							 claimAdmissionDetails.setIcuStayDuration(attachmentDTO.getIcuStayDuration());
							 claimAdmissionDetails.setIcuServiceTypeId(attachmentDTO.getIcuServiceTypeId());
							 claimAdmissionDetails.setRoomType(attachmentDTO.getRoomType());

							 hospitalServiceType.setRoomType(attachmentDTO.getRoomType());
							 hospitalServiceType.setVitrayaRoomCategory(VitrayaRoomCategory.valueOf(attachmentDTO.getVitrayaRoomCategory()));
							 hospitalServiceType.setInsurerRoomType(attachmentDTO.getInsurerRoomType());
							 hospitalServiceType.setSinglePrivateAC(attachmentDTO.isSinglePrivateAC());
							 hospitalServiceType.setServiceType(attachmentDTO.getServiceType());

							 illness.setIllnessCategoryId(attachmentDTO.getIllnessCategoryId());
							 illness.setIllnessName(attachmentDTO.getIllnessName());
							 illness.setDefaultICDCode(attachmentDTO.getDefaultICDCode());

							 procedureMethod.setProcedureCode(attachmentDTO.getProcedureCode());

							 preAuthDetails.setServiceTypeId(attachmentDTO.getServiceTypeId());
							 preAuthDetails.setDocumentMasterList(attachmentDTO.getDocumentMasterList());
						 }
						 else if(coding.getCode().equalsIgnoreCase("TRD-3")){
							 claimIllnessTreatmentDetails.setLineOfTreatmentDetails(supportingInfo.getValueStringType().getValue());
						 }
					 }


				}

			}
			else if(resourceType.equalsIgnoreCase("patient")){
                Patient patient= (Patient) entryComponent.getResource();
				vhiClaim.setHospitalPatientId(patient.getIdentifier().get(0).getValue());
				vhiClaim.setDob(DateUtils.formatDate(patient.getBirthDate()));
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
				vhiProcedure.setDescription(procedure.getNote().get(0).getText());
				vhiProcedure.setName(procedure.getCode().getText());
				procedureMethod.setProcedureMethodName(procedure.getCode().getText());

			}
			else if(resourceType.equalsIgnoreCase("organization")){
				Organization organization= new Organization();
				if(entryComponent.getFullUrl().contains("InsurerOrganization")){
					organization= (Organization) entryComponent.getResource();
					vhiClaim.setInsuranceAgencyId(Integer.valueOf(organization.getIdentifier().get(0).getValue()));
				}
				else if(entryComponent.getFullUrl().contains("ProviderOrganization/1")){
					organization= (Organization) entryComponent.getResource();
					List<Identifier> identifierList= organization.getIdentifier();
					for(Identifier identifier:identifierList){
						List<Coding> codingList=identifier.getType().getCoding();
						for(Coding code:codingList){
							if(code.getCode().equalsIgnoreCase("PRN")){
								vhiClaim.setHospitalId(Integer.valueOf(identifier.getValue()));
							}
						}
					}
					vhiClaim.setCityName(organization.getContact().get(0).getPurpose().getText());
				}

			}
			else if(resourceType.equalsIgnoreCase("Practitioner")){
                Practitioner practitioner= (Practitioner) entryComponent.getResource();
                List<HumanName> nameList=practitioner.getName();
                List<Practitioner.PractitionerQualificationComponent> qualificationList= practitioner.getQualification();
				DoctorDetailsDto doctor= new DoctorDetailsDto();
				for(HumanName name:nameList){
					doctor.setDoctorName(name.getGivenAsSingleString());
				}
				for(Practitioner.PractitionerQualificationComponent qualification:qualificationList){
					doctor.setQualification(qualification.getCode().getText());
				}
                claimIllnessTreatmentDetails.setDoctorsDetails(new Gson().toJson(doctor));

				List<Identifier> identifierList= practitioner.getIdentifier();
				for(Identifier identifier:identifierList){
					List<Coding> codingList=identifier.getType().getCoding();
					 for(Coding code:codingList){
						 if(code.getCode().equalsIgnoreCase("PLAC")){
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
				claimIllnessTreatmentDetails.setDateOfDiagnosis(String.valueOf(condition.getRecordedDate()));
			}
			else if(resourceType.equalsIgnoreCase("Coverage")){
				Coverage coverage= (Coverage) entryComponent.getResource();
				vhiClaim.setMedicalCardId(coverage.getSubscriberId());
				vhiClaim.setPolicyNumber(coverage.getIdentifier().get(0).getValue());
				vhiClaim.setPolicyType(PolicyType.valueOf(coverage.getType().getText()));
				vhiClaim.setPolicyEndDate(DateUtils.formatDate(coverage.getPeriod().getEnd()));
				vhiClaim.setPolicyName(coverage.getClass_().get(0).getValue());
				vhiClaim.setPolicyStartDate(DateUtils.formatDate(coverage.getPeriod().getStart()));
			}

		}
		preAuthDetails.setClaim(vhiClaim);
		preAuthDetails.setClaimIllnessTreatmentDetails(claimIllnessTreatmentDetails);
		preAuthDetails.setClaimAdmissionDetails(claimAdmissionDetails);
		preAuthDetails.setHospitalServiceType(hospitalServiceType);
		preAuthDetails.setProcedure(vhiProcedure);
		preAuthDetails.setProcedureMethod(procedureMethod);
		preAuthDetails.setIllness(illness);
		log.info("preAuthDetails{}",new Gson().toJson(preAuthDetails));
	}
	public static void main(String[] args) throws Exception {

		SpringApplication.run(HcxproviderApplication.class, args);
		getJWEResponsePayload();
		buildVhiClaimRequest();
	}

}
