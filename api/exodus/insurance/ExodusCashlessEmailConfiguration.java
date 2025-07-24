package com.halodoc.batavia.controller.api.exodus.insurance;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExodusCashlessEmailConfiguration {

    @JsonProperty("claim_drafted_op")
    private boolean claimDraftedOp;

    @JsonProperty("claim_drafted_op_recipient")
    private String claimDraftedOpRecipient;

    @JsonProperty("claim_drafted_ip")
    private boolean claimDraftedIp;

    @JsonProperty("claim_drafted_ip_recipient")
    private String claimDraftedIpRecipient;

    @JsonProperty("claim_verification_pending")
    private boolean claimVerificationPending;

    @JsonProperty("claim_verification_pending_recipient")
    private String claimVerificationPendingRecipient;

    @JsonProperty("igl_approved_op")
    private boolean IGLApprovedOp;

    @JsonProperty("igl_approved_op_recipient")
    private String IGLApprovedOpRecipient;

    @JsonProperty("igl_approved_ip")
    private boolean IGLApprovedIp;

    @JsonProperty("igl_approved_ip_recipient")
    private String IGLApprovedIpRecipient;

    @JsonProperty("igl_rejected_op")
    private boolean IGLRejectedOp;

    @JsonProperty("igl_rejected_op_recipient")
    private String IGLRejectedOpRecipient;

    @JsonProperty("igl_rejected_ip")
    private boolean IGLRejectedIp;

    @JsonProperty("igl_rejected_ip_recipient")
    private String IGLRejectedIpRecipient;

    @JsonProperty("fgl_approved_op")
    private boolean FGLApprovedOp;

    @JsonProperty("fgl_approved_op_recipient")
    private String FGLApprovedOpRecipient;

    @JsonProperty("fgl_approved_ip")
    private boolean FGLApprovedIp;

    @JsonProperty("fgl_approved_ip_recipient")
    private String FGLApprovedIpRecipient;

    @JsonProperty("fgl_rejected_op")
    private boolean FGLRejectedOp;

    @JsonProperty("fgl_rejected_op_recipient")
    private String FGLRejectedOpRecipient;

    @JsonProperty("fgl_rejected_ip")
    private boolean FGLRejectedIp;

    @JsonProperty("fgl_rejected_ip_recipient")
    private String FGLRejectedIpRecipient;

    @JsonProperty ("admission_approved_ip")
    private boolean admissionApprovedIp;

    @JsonProperty ("admission_rejected_ip")
    private boolean admissionRejectedIp;

    @JsonProperty ("waiting_document_completion_ip")
    private boolean waitingDocumentCompletionIp;

    @JsonProperty ("document_completed_ip")
    private boolean documentCompletedIp;

    @JsonProperty ("all_rejected_ip")
    private boolean allRejectedIp;

    @JsonProperty ("discharge_requested_ip")
    private boolean dischargeRequestedIp;

    @JsonProperty ("discharge_completed_ip")
    private boolean dischargeCompletedIp;

    @JsonProperty ("admission_approved_ip_recipient")
    private String admissionApprovedIpRecipient;

    @JsonProperty ("admission_rejected_ip_recipient")
    private String admissionRejectedIpRecipient;

    @JsonProperty ("waiting_document_completion_ip_recipient")
    private String waitingDocumentCompletionIpRecipient;

    @JsonProperty ("document_completed_ip_recipient")
    private String documentCompletedIpRecipient;

    @JsonProperty ("all_rejected_ip_recipient")
    private String allRejectedIpRecipient;

    @JsonProperty ("discharge_requested_ip_recipient")
    private String dischargeRequestedIpRecipient;

    @JsonProperty ("discharge_completed_ip_recipient")
    private String dischargeCompletedIpRecipient;

}
