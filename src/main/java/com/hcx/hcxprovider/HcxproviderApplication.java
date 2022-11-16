package com.hcx.hcxprovider;

import io.hcxprotocol.impl.HCXOutgoingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.Operations;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
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
		//String payload = "{\"resourceType\":\"Bundle\",\"id\":\"9699444a\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2021-12-22T15:32:26.605+05:30\"},\"timestamp\":\"2021-12-22T15:32:26.605+05:30\",\"identifier\":{\"system\":\"urn:ietf:rfc:3986\",\"value\":\"urn:uuiad:9699444a\"},\"type\":\"document\",\"entry\":[{\"fullUrl\":\"Composition/1\",\"resource\":{\"resourceType\":\"Composition\",\"id\":\"1\",\"date\":\"2021-12-22T15:32:26.605+05:30\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2021-12-22T15:32:26.605+05:30\"},\"identifier\":{\"system\":\"urn:ietf:rfc:3986\",\"value\":\"urn:uuid:28b6b35e\"},\"status\":\"final\",\"type\":{\"coding\":[{\"system\":\"https://hcx.org/document-types\",\"code\":\"HCXClaimResponse\",\"display\":\"ClaimResponse\"}],\"text\":\"ClaimResponse\"},\"subject\":{\"reference\":\"Patient/1\",\"display\":\"HinaPatel\"},\"author\":[{\"reference\":\"Practitioner/1\",\"display\":\"Dr.SudhirNaik(PMJAYGujrat)\"}],\"title\":\"ClaimRequest\",\"attester\":[{\"mode\":\"official\",\"time\":\"2021-12-22T14:10:14Z\",\"party\":{\"reference\":\"Organization/2\",\"display\":\"PMJAY,MoHFW,GovtOfIndia\"}}],\"section\":[{\"title\":\"ClaimResponse\",\"code\":{\"coding\":[{\"system\":\"https://www.hl7.org/fhir/valueset-resource-types\",\"code\":\"ClaimResponse\",\"display\":\"ClaimResponse\"}]},\"entry\":[{\"reference\":\"ClaimResponse/res-id-1\"}]}]}},{\"fullUrl\":\"ClaimResponse/ress-id-1\",\"resource\":{\"resourceType\":\"ClaimResponse\",\"id\":\"1355110\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2020-07-03T22:50:39.698+00:00\",\"source\":\"#JrkobxCRdZUI6QNh\"},\"status\":\"active\",\"type\":{\"coding\":[{\"code\":\"institutional\"}]},\"use\":\"preauthorization\",\"patient\":{\"reference\":\"Patient/1353519\",\"type\":\"Patient\"},\"created\":\"2020-07-03T18:50:39-04:00\",\"insurer\":{\"reference\":\"Organization/185\",\"type\":\"Organization\"},\"request\":{\"reference\":\"Claim/1355109\"},\"outcome\":\"complete\",\"item\":[{\"itemSequence\":1,\"adjudication\":[{\"category\":{\"coding\":[{\"code\":\"eligible\"}]},\"amount\":{\"value\":500000,\"currency\":\"INR\"}},{\"category\":{\"coding\":[{\"code\":\"copay\"}]},\"amount\":{\"value\":50000,\"currency\":\"INR\"}},{\"category\":{\"coding\":[{\"code\":\"eligpercent\"}]},\"value\":80.00},{\"category\":{\"coding\":[{\"code\":\"benefit\"}]},\"reason\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/adjudication-reason\",\"code\":\"ar002\",\"display\":\"PlanLimitReached\"}]},\"amount\":{\"value\":360000,\"currency\":\"INR\"}}]}],\"total\":[{\"category\":{\"coding\":[{\"code\":\"submitted\"}]},\"amount\":{\"value\":500000,\"currency\":\"INR\"}},{\"category\":{\"coding\":[{\"code\":\"benefit\"}]},\"amount\":{\"value\":360000,\"currency\":\"INR\"}}],\"payment\":{\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/ex-paymenttype\",\"code\":\"complete\"}]},\"date\":\"2014-08-31\",\"amount\":{\"value\":410000,\"currency\":\"INR\"},\"identifier\":{\"system\":\"http://www.BenefitsInc.com/fhir/paymentidentifier\",\"value\":\"201408-2-1569478\"}}}},{\"fullUrl\":\"Patient/1\",\"resource\":{\"resourceType\":\"Patient\",\"id\":\"1\",\"name\":[{\"use\":\"official\",\"family\":\"Patel\",\"given\":[\"Hina\"]}],\"identifier\":[{\"type\":{\"coding\":[{\"system\":\"http://ndhm.gov.in/ValueSet/identifier-type\",\"code\":\"PHRADDR\"}]},\"system\":\"http://ndhm.gov.in/healthId\",\"value\":\"654321\"},{\"type\":{\"coding\":[{\"system\":\"http://ndhm.gov.in/ValueSet/identifier-type\",\"code\":\"PMJAYID\"}]},\"system\":\"http://ndhm.gov.in/pmjay\",\"value\":\"QWRT12345\"}],\"gender\":\"female\",\"birthDate\":\"1974-12-25\"}},{\"fullUrl\":\"Organization/MaxSaket01\",\"resource\":{\"resourceType\":\"Organization\",\"id\":\"MaxSaket01\",\"name\":\"MaxSuperSpecialityHospital,Saket\",\"identifier\":[{\"system\":\"https://ndhm.gov.in/hfr\",\"value\":\"HFR-10000005\"}]}},{\"fullUrl\":\"Organization/2\",\"resource\":{\"resourceType\":\"Organization\",\"id\":\"2\",\"name\":\"PMJAY,MoHFW,GovtOfIndia\",\"identifier\":[{\"system\":\"https://hcx.org/orgs\",\"value\":\"PMJAY\"}]}},{\"fullUrl\":\"Practitioner/1\",\"resource\":{\"resourceType\":\"Practitioner\",\"id\":\"1\",\"identifier\":[{\"system\":\"https://ndhm.gov.in/npr\",\"value\":\"NPR-6897\"}],\"name\":[{\"text\":\"SudhirNaik(PMJAYGujrat)\",\"prefix\":[\"Dr\"],\"suffix\":[\"MD\"]}]}}]}";
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
