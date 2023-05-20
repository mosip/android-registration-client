package io.mosip.registration.packetmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakerReviewerDetails {

    private String makerId;
    private String reviewerId;
    private String makerAuth;
    private String reviewerAuth;
    private List<Biometrics> makerBiometrics;
    private List<Biometrics> reviewerBiometrics;
}
