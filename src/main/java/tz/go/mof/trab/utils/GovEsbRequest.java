package tz.go.mof.trab.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class GovEsbRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "api_code")
    private String apiCode;

    @Column(name = "esb_request", columnDefinition = "text")
    private String esbRequest;

    @Column(name = "esb_response", columnDefinition = "text")
    private String esbResponse;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "signature_verified")
    private Boolean signatureVerified;

    @Column(name = "esb_request_uid")
    private String esbRequestUid;

    @Column(name = "is_charged")
    private Boolean isCharged;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private EsbRequestTypeEnum requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "institution")
    private GovEsbInstitutionEnum institutionEnum;

    public GovEsbRequest(String apiCode, String esbRequest, EsbRequestTypeEnum requestType, GovEsbInstitutionEnum institutionEnum) {
        this.apiCode = apiCode;
        this.esbRequest = esbRequest;
        this.requestType = requestType;
        this.institutionEnum = institutionEnum;
        this.success = false;
        this.isCharged = false;
        this.signatureVerified = false;
    }
}
