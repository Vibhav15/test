package com.halodoc.batavia.controller.api.exodus.insurance;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExodusEnrollmentEmailConfiguration {
    private boolean enrollment;

    @JsonProperty("enrollment_recipient")
    private String EnrollmentRecipient;

}
