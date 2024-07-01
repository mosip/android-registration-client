package io.mosip.registration.clientmanager.dto;

import java.util.List;

/**
 * The DTO Class ResponseDTO.
 *
 * @author Sreekar Chukka
 * @version 1.0.0
 */
public class ResponseDto {
    private List<ErrorResponseDto> errorResponseDTOs;
    private SuccessResponseDto successResponseDTO;
    public List<ErrorResponseDto> getErrorResponseDTOs() {
        return errorResponseDTOs;
    }
    public void setErrorResponseDTOs(List<ErrorResponseDto> errorResponseDTOs) {
        this.errorResponseDTOs = errorResponseDTOs;
    }
    public SuccessResponseDto getSuccessResponseDTO() {
        return successResponseDTO;
    }
    public void setSuccessResponseDTO(SuccessResponseDto successResponseDTO) {
        this.successResponseDTO = successResponseDTO;
    }

    @Override
    public String toString() {
        if(this.errorResponseDTOs != null && !this.errorResponseDTOs.isEmpty()) {
            return this.errorResponseDTOs.get(0).getCode() + ":" + this.errorResponseDTOs.get(0).getMessage();
        }

        if(this.successResponseDTO != null)
            return this.successResponseDTO.getCode() + ":" + this.successResponseDTO.getMessage();

        return super.toString();
    }

}
