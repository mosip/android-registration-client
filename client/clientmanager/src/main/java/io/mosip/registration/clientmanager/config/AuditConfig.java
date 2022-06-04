package io.mosip.registration.clientmanager.config;


/**
 * The configuration class for Audit having package location to scan
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */

public class AuditConfig {

	/**
	 * Creates a new Modelmapper bean
	 *
	 * @return The {@link ModelMapper}
	 */
 	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
