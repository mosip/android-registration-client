# Design - Registration Center Machine Re-mapping

## Background
The administrator has the ability to remap a registration machine to any desiered center. When this happens the current captured data should be moved to server without any failure before refreshing the master data in the registration machine. This design provides the details about the process of re-mapping the machine from one center to another center in Registration Client.

## Taget Users
-  System [Registration Client]
-  Operator (Officer/Supervisor)

## Key Requirements

* If the system receives details stating that it has been re-mapped to a new registration center, the following activities will be disabled for the operator:
    * NEW Registration
    * UIN Update
    * Lost UIN
    * Operator on-boarding
    * Pre-registration data download
    * Operator biometric update
* The operator should be able to perform pending activities such as:
  * Packet approval (EOD)
  * Sending re-registration notification to Resident
* When the system is online and there are no pending activities a one-time background process should:
  * Push packet IDs to server
  * Push the packets to server
  * Push user on-boarding data to the server
  * Delete all the data except audit data
* Once the one-time background process is executed successfully, the operator from the old registration center would not be allowed to login to the system.
* If the one-time process is not yet run, the operator will still be able to
  * Login to the system
  * Perform sync
  * Perform EOD approval
  * Export and Upload packets

## Solution

* As part of the master sync using the Machine ID + Center ID, if the response contains the string which relevant to the center re-mapping:
    * Then update the property [**'mosip.registrtaion.centermappedchanged'**] value as 'true' in the GLOBAL_PARAM table.
        * Display the alert message as 'You machine has been re-mapped to another center, kindly complete the pending activities'.
        * Stop the auto sync process by updating the de-activation flag. Henceforth, only manual sync process can only be performed until the machine mapping completed.
    * Maintain this status flag in the session context to identify the 'Center Mapping' state.
* When operator clicks on the [New Registration/UIN Update/Lost UIN], if the property [**'mosip.registrtaion.centermappedchanged'**] has value 'true', then don't allow the operator to proceed.
* **Triggering Point:** When the operator clicks on the 'Machine Re-mapping' option in the menu or initiates [New Registration/UIN Update/Lost UIN] the process, if remapping flag is 'true' then invoke the CenterRemappingService - with processCenterMapped() method and display the message as 'Machine cleanup process has been started due to remapping'.
* Create the CenterRemappingService - with processCenterMapped() method, which does the following functionalities. If the system is online and the operator is not in middle between any of the operations [New Registration/UIN Update/Lost UIN] then do the below process as sequence steps.
  * Check for any pending packets to be uploaded to the server and waiting for 'PROCESSING' status.   
  * Check for any packets to be approved/ rejected/ upload / sync to the server based on 'Server Status' flag.    
  * Delete the packets and records in the table as below:
  * Pre-registration: all packets can be deleted from hard disk and the respective data can be cleaned from tables.
  * Registration {New/Update/Lost}: all packets in the state of [Server Status] 'PROCESSING', 'PROCESSED', 'RE-REGISTER' can be deleted.
  * Center specific master tables can be deleted.
  * Don't delete the data from 'GLOBAL_PARAM' table as this is not specific to any center.
  * Don't delete record in AUDIT table.
  * Once all deletion completed:
  * Remove all users and their respective detail with respect to the current center.
  * Update the 'mosip.registrtaion.centermappedchanged' flag as false.
  * Enable the sync process.
  * Display valid alert [information type] message to inform the user as process completed.
* If the system is offline we should wait until the system is online and then only this process should initiate.
* While doing this process we should display the alert stating  **'Upload is going on. Please don't close the application'**.
* Progress bar or uploading image should be displayed in the screen and the background should be fade out.
* Please create the **reg_machine_center_changed.sql** and added to the module **registration-services** --> **src/main/resources**.
* All events should be logged in the AUDIT table.

List of **Packet Status** from server:

	**RECEIVED**   	:	Successfully uploaded the packet to server. Virus Scan and Decryption not yet started.
	**RE-SEND**    	:	Virus Scan or Decryption failed.
	**PROCESSING**	:	After Virus Scanner and Decryption successfully completed and until the UIN Generation.
	**PROCESSED**	:	UIN Generated successfully.
	**RE-REGISTER**	:	If any structural validation fails.
