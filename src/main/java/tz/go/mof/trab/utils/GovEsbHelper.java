package tz.go.mof.trab.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Component
public class GovEsbHelper {

    private static final Logger log = LoggerFactory.getLogger(GovEsbHelper.class);

    private GePGGlobalSignature gePGGlobalSignature;

    @Value("${govesb.client-private-key}")
    public String clientPrivateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDXNE/yHQ+I3I0+tiq3hPoFF4nXDsk5AnqmpQHmqkmOJ5kAP+gB+tJq1dpVXDxZ/SFCbgHnUK4TsDbYbemP336vAaq0RSqzUR58tmmI4Q4hkCjc1V1aThojH1hHhNR41i1MXZfallZY3BfoIEydI/MyTL1BDUV6RFSrHffT/cKEiMgM9TNF91dGZhsB04/okoNKDb18C6JI8s5uUsiF7QKHDDMxbBIzHAujYZBZHx5EMsWvwkycO/OYO8n/NumCs32bclAbgdXHoUw790OrIe7X2x0XP1GoLARKpRspDi9aitc0z5a6cd3CzSzINrRHTFk1NzJEFKmKG+r0/f40P8mvAgMBAAECggEAeBraA2X0pY1xxu4kKQguAWmBpGS8KPUZPqgYx4OCSbQPz/PaqEAz7ywrDjjL9e0wAsMijNaaUKKn+WJeV82EBmgHvVRc4HcFbNThUWNJX7H1HEp4L04niDKWMlQPwODHpzcU6Qm+Zxluvj5Ig+rsb8YkWYnunK5GErpG9ilJxm0KNLhv98LQ4PcCsMIDFVBFlaJgoBUOMZK6Howzq0B/O+RrwUbhbbujsxt5B4AvR8jU3bVTneN+jNKdgvyIvFfqiCTTICiIJp4p+rHVfbWYT45JP+BdIlN234HzOTyrLAnd3euS+mCmZVo8MQNIAYUF5lTHOjRGr3RvOoH17X93AQKBgQDt3NCadTf1ZPsSN6VqoZlDYe9lCRdsc/2FgucNjjU9Ml+Bs3jQfpS+Hp80+YvEGckKqlnnJh3SakKYPFgZ1XualZEf9fJNjsxmIiLsgWB4B/P+RDIr/uwai/G0ZHP5IOl6fTjj+g+2J7n4mBfgPLL0XtLgpbCf5mAyFOwzxs7KYwKBgQDnnTLl+O5bIonquSAe17+AJTUI6/iW/URD3w7g4PSeKTLMjX9xtwNC44FR8hnMyJerEsZX43puVfG7IPrzHTMioW7FgEy69SXcS830uwk0evij/Rc/LNQ66fpE+11CtORCrpBPCtQmqvj9AxsR0vaDqofV4Z0k8CRTiQxM5R3fRQKBgQC1PG2Zcr0LdkCzVcDtKWoM5H8yfaXiVdKSuvXv5y37jfklaykfu4L8BXsLMyTiaz86qCfOLsXEQICzXC9Ip9gBJfso3g4cODTXkSUJDV8CthSb5TxRN3CQnZax+2Q+K2yajvUdB7iW81gI6WIv0jAMcH+++aBFhZRANJ7wqIeBvwKBgQDIzcZaEdb9FlXCNdw+QUjbHeb8JDFzHoM34MSyfPY3xXowVCEBAu2F6V6uIBr0K5krDWz+t87mqAaa+X34lwFFbfZursxXbsDzXJlkK2BQqqi07HFiKUibB04ezltNwS0BDv04OigymhqiqTWn32IwRwHbke4/HyAveGs8o4uhjQKBgQDBHmrrpHHw+ILZKkzS3kNGnjuqQQFBbetZrnIjsrfwc+lvz7YH4ONpKuuYpPgaisHd2ME3Q5aLY5jAXHC6BjZN4HlvmZsq9tuIcueSMoaVmQ5vGfO/l9UXo2BmoB/e8mz4t02EMtHSo7yb261FpqGAc/ASiWQiy7Ll6VYpx2Ufkg==";

    @Value("${govesb.public-key}")
    public String esbPublicKey = "MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQACEp3tk19VUsQzX8v+pQW3P+aqONjHGoBEc8YPD4KZytxGyvwAL73HKceFYkrGFnh0PkZTh4U0UxaIZ0oQr53cHAAwCpZxMrBmULlHJWbowcjgBqk0sbgMx4u259bPbFmjRZXwfYQ9NDvDYuy+QOLjZcxpAcx0PkYTugJgGuVYrtp9Xo=";

    @Value("${govesb.client-id}")
    public String clientId = "a3a2d96d-f2ac-11ef-b6e4-61bd09ed9d56";

    @Value("${govesb.client-secret}")
    public String clientSecret = "Zd7IVgDHH56MhLbIuWHHggd29agkMaFU";

    @Value("${govesb.token-url}")
    public String esbTokenUrl = "https://esbdemo.gov.go.tz/gw/govesb-uaa/oauth/token";

    @Value("${govesb.engine-url}")
    public String esbEngineUrl = "https://esbdemo.gov.go.tz/engine/esb";

    @Value("${govesb.nida-user-id}")
    public String nidaUserId;

    @Value("${govesb.tin.api.code}")
    private String tinInfoApiCode = "IBCV27zZ";

    @Value("${govesb.organization.id}")
    private String traOrganizationId = "1";

    public String apiCode = "QVVXb9tS";
    public String requestBody;
    public String format = "json";
    public String accessToken;

    @Autowired
    private ResourceLoader resourceLoader;

    public JsonNode getAccessToken() throws Exception {
        String plainCredentials = this.clientId + ":" + this.clientSecret;
        String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
        String authorizationHeader = "Basic " + base64Credentials;

        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(this.esbTokenUrl);

            ArrayList<BasicNameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("client_id", this.clientId));
            nvps.add(new BasicNameValuePair("client_secret", this.clientSecret));
            nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));

            request.setEntity(new UrlEncodedFormEntity(nvps));
            request.addHeader("Authorization", authorizationHeader);
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenResponse = mapper.readTree(responseString);

            if (response.getStatusLine().getStatusCode() == 200) {
                if (tokenResponse.has("access_token")) {
                    this.accessToken = tokenResponse.get("access_token").asText();
                }
            } else {
                throw new Exception("Could not get access token from esb");
            }

            System.out.println("Token response: " + tokenResponse);
            return tokenResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public String requestData(String apiCode, String requestBody, String format) throws Exception {
        return this.request(apiCode, requestBody, format, false, null, this.esbEngineUrl + "/request", null);
    }

    public String requestData(String apiCode, String requestBody, String format, HashMap<String, String> headers) throws Exception {
        return this.request(apiCode, requestBody, format, false, null, this.esbEngineUrl + "/request", headers);
    }

    public String requestNida(String apiCode, String requestBody, String format) throws Exception {
        if (this.nidaUserId == null) {
            throw new Exception("nidaUserId is required");
        }
        return this.request(apiCode, requestBody, format, false, this.nidaUserId, this.esbEngineUrl + "/nida-request", null);
    }

    public String pushData(String apiCode, String requestBody, String format) throws Exception {
        return this.request(apiCode, requestBody, format, true, null, this.esbEngineUrl + "/push-request", null);
    }

    private String request(String apiCode, String requestBody, String format, boolean isPushRequest,
                           String nidaUserId, String esbRequestUrl, HashMap<String, String> headers) throws Exception {
        this.initializeRequest(apiCode, requestBody, format);
        String esbRequestBody = this.createEsbRequest(isPushRequest, nidaUserId);
        System.out.println("Request to GovESB: " + esbRequestBody);
        String esbResponse = this.sendEsbRequest(esbRequestBody, esbRequestUrl, headers);
        System.out.println("GovESB Response: " + esbResponse);
        return this.verifyThenReturnData(esbResponse, this.format);
    }

    public String successResponse(String requestBody, String format) throws Exception {
        return this.esbResponse(true, requestBody, null, format, false);
    }

    public String failureResponse(String requestBody, String message, String format) throws Exception {
        return this.esbResponse(false, requestBody, message, format, false);
    }

    public String handledFailureResponse(String requestBody, String message, String format) {
        try {
            return this.esbResponse(false, requestBody, message, format, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String asyncSuccessResponse(String requestBody, String format) throws Exception {
        return this.esbResponse(true, requestBody, null, format, true);
    }

    public String asyncFailureResponse(String requestBody, String message, String format) throws Exception {
        return this.esbResponse(false, requestBody, message, format, true);
    }

    private String esbResponse(boolean isSuccess, String requestBody, String message, String format,
                                boolean isAsyncResponse) throws Exception {
        return this.createEsbResponse(isSuccess, requestBody, message, isAsyncResponse);
    }

    private String createEsbResponse(boolean isSuccess, String requestBody, String message,
                                      boolean isAsyncResponse) throws Exception {
        if (this.format.equals("json")) {
            return this.createJsonResponse(isSuccess, requestBody, message, isAsyncResponse);
        }
        return null;
    }

    private String createJsonResponse(boolean isSuccess, String requestBody, String message,
                                       boolean isAsyncResponse) throws Exception {
        JsonMapper mapper = new JsonMapper();
        ObjectNode dataNode = this.createResponseData(isSuccess, requestBody, message, isAsyncResponse);
        String signature = this.signData(dataNode.toString());

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.set("data", dataNode);
        responseNode.put("signature", signature);
        return responseNode.toString();
    }

    private ObjectNode createResponseData(boolean isSuccess, String requestBody, String message,
                                           boolean isAsyncResponse) throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();
        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("success", isSuccess);

        if (requestBody != null) {
            JsonNode esbBodyNode = mapper.readTree(requestBody);
            dataNode.set("esbBody", esbBodyNode);
        }

        if (message != null && !isSuccess) {
            dataNode.put("message", message);
        }

        if (isAsyncResponse && isSuccess) {
            dataNode.put("requestId", this.apiCode);
        }

        return dataNode;
    }

    public String verifyThenReturnData(String esbResponse, String format) throws JsonProcessingException {
        if (format != null) {
            this.format = format;
        }
        ObjectMapper mapper = this.getMapper();
        JsonNode node = mapper.readTree(esbResponse);
        String data = "";
        String signature = node.get("signature").toString();

        data = this.format.equals("json")
                ? node.get("data").toString()
                : mapper.writer().withRootName("data").writeValueAsString(node.get("data"));

        boolean isValid = this.verifyPayloadECC(data, signature);
        if (!isValid) {
            System.out.println("Signature verification failed!");
        }
        return data;
    }

    private ObjectMapper getMapper() {
        return new JsonMapper();
    }

    private void initializeRequest(String apiCode, String requestBody, String format) throws Exception {
        this.assertNotNull();
        this.getAccessToken();
        this.validateRequestParameters(apiCode, requestBody, format);
    }

    private String sendEsbRequest(String requestBody, String esbRequestUrl, HashMap<String, String> headers) {
        String esbResponse = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(esbRequestUrl);
            request.addHeader("Authorization", "Bearer " + this.accessToken);
            request.addHeader("Content-Type", "application/" + this.format);

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.addHeader(header.getKey(), header.getValue());
                }
            }

            StringEntity requestEntity = new StringEntity(requestBody);
            request.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(request);
            esbResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Response from GovESB: " + esbResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return esbResponse;
    }

    private String createEsbRequest(boolean isPushRequest, String userId) throws Exception {
        if (this.format.equals("json")) {
            return this.createJsonRequest(isPushRequest, userId);
        }
        return null;
    }

    private String createJsonRequest(boolean isPushRequest, String userId) throws Exception {
        ObjectNode esbRequestNode = this.createEsbData(isPushRequest, userId);
        String payload = esbRequestNode.toString();
        String signature = this.signData(payload);

        ObjectNode node = new JsonMapper().createObjectNode();
        node.set("data", esbRequestNode);
        node.put("signature", signature);
        return node.toString();
    }

    private ObjectNode createEsbData(boolean isPushRequest, String userId) throws JsonProcessingException {
        JsonMapper mapper = new JsonMapper();
        ObjectNode esbRequestNode = mapper.createObjectNode();
        esbRequestNode.put(isPushRequest ? "pushCode" : "apiCode", this.apiCode);

        if (userId != null) {
            esbRequestNode.put("userId", this.nidaUserId);
            ObjectNode payloadNode = mapper.createObjectNode();
            payloadNode.set("Payload", mapper.readTree(this.requestBody));
            esbRequestNode.set("esbBody", payloadNode);
        } else if (this.requestBody != null) {
            esbRequestNode.put("esbBody", mapper.readTree(this.requestBody));
        }

        return esbRequestNode;
    }

    private String signData(String payload) throws Exception {
        return this.signPayloadECC(payload);
    }

    private String signPayloadECC(String payload) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode("MGACAQAwEAYHKoZIzj0CAQYFK4EEACMESTBHAgEBBEIBReH6Xq3caCbgYIq7ArVgbGIW8gO6mMrWh9MeRWL0ejS+0yoZCT4QDFsvuD2qz+0wXDYCyxWSZrmAOHmHkVkRuQ0="));
        PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);

        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(payload.getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsaSign.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    private boolean verifyPayloadECC(String data, String signature) {
        try {
            Signature ecdsaVerifySignature = Signature.getInstance("SHA256withECDSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                    Base64.getDecoder().decode(this.esbPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            ecdsaVerifySignature.initVerify(publicKey);
            ecdsaVerifySignature.update(data.getBytes(StandardCharsets.UTF_8));
            return ecdsaVerifySignature.verify(Base64.getMimeDecoder().decode(signature));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void validateRequestParameters(String apiCode, String requestBody, String format) throws Exception {
        if (apiCode != null && !apiCode.isEmpty()) {
            this.apiCode = apiCode;
        }
        if (requestBody != null && !requestBody.isEmpty()) {
            this.requestBody = requestBody;
        }
        if (format != null && !format.isEmpty()) {
            this.format = format;
        }
        if (this.apiCode == null) {
            throw new Exception("apiCode can not be null");
        }
        if (this.format == null || (!this.format.equalsIgnoreCase("json") && !this.format.equalsIgnoreCase("xml"))) {
            throw new Exception("format can not be null");
        }
    }

    private void assertNotNull() throws Exception {
        if (this.clientId == null || this.clientSecret == null || this.clientPrivateKey == null ||
                this.esbPublicKey == null || this.esbTokenUrl == null || this.esbEngineUrl == null) {
            throw new Exception("Some EsbHelper properties are null: make sure all required EsbHelper properties are set");
        }
    }

    public String getFormat() {
        return this.format;
    }

    public String getEsbData(String dataBody, String format, String field) throws JsonProcessingException {
        if (format != null) {
            this.format = format;
        }
        ObjectMapper mapper = this.getMapper();
        JsonNode node = mapper.readTree(dataBody);
        String data = "";
        if (this.format.equals("json")) {
            if (node.get(field) != null) {
                data = node.get(field).toString();
            }
        } else {
            data = mapper.writer().withRootName(field).writeValueAsString(node.get(field));
        }
        return data;
    }

    public String getTinDetails(@NotBlank String tinNumber) {
        log.info("[GOV ESB]: Requesting TRA information by tin number {}", tinNumber);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tinNumberNode = mapper.readTree(tinNumber);

            JSONObject payload = new JSONObject();
            payload.put("requestData", tinNumberNode.get("tinNumber").asText());
            payload.put("requestOrganization", "TRAB");

            String esbRequestBody = payload.toString();
            GovEsbRequest govEsbRequest = new GovEsbRequest(this.tinInfoApiCode, esbRequestBody,
                    EsbRequestTypeEnum.ESB_REQUEST, GovEsbInstitutionEnum.TRA);

            String esbResponse = this.requestData(this.tinInfoApiCode, payload.toString(), "json");
            JsonNode responseNode = mapper.readTree(esbResponse);

            govEsbRequest.setSignatureVerified(true);
            govEsbRequest.setEsbRequestUid(responseNode.get("requestId").asText());

            JsonNode jsonEncryptedObjData = mapper.readTree(responseNode.get("esbBody").toString());
            String responseKey = jsonEncryptedObjData.get("responseKey").asText();
            String responseData = jsonEncryptedObjData.get("responseData").asText();

            String decryptKeyString = RSADecryption(
                    this.getPrivateKeyFromPfx("/Users/mwendavano/trat/TRAB.pfx", "Kibamb@23"), responseKey);
            String decryptedData = AESDecryption(decryptKeyString, responseData);

            log.info(decryptedData);
            return decryptedData;
        } catch (Exception e) {
            log.error("GOVESB-TRA", e);
            return null;
        }
    }

    private RSAPrivateKey getPrivateKeyFromPfx(String privatePfxFile, String password) {
        try {
            KeyStore pfxKeyStore = KeyStore.getInstance("PKCS12");
            Resource resource = this.resourceLoader.getResource("classpath:" + privatePfxFile);
            pfxKeyStore.load(resource.getInputStream(), password.toCharArray());
            String alias = pfxKeyStore.aliases().nextElement();
            return (RSAPrivateKey) pfxKeyStore.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            log.error("GOVESB-TRA", e);
            return null;
        }
    }

    private static String RSADecryption(RSAPrivateKey privateKey, String cypherText) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(cypherText);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1",
                    new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("GOVESB-TRA", e);
            return null;
        }
    }

    private static String AESDecryption(String key, String cypherText) {
        try {
            IvParameterSpec ivParameter = new IvParameterSpec(key.substring(0, 16).getBytes());
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), 0, key.getBytes().length, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameter);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cypherText));
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("GOVESB-TRA", e);
            return null;
        }
    }
}
