package com.ewsaly.ewsaly.service.emergency;

import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;

import java.util.List;

public interface EmergencyContactService {

    EmergencyContactResponse addEmergencyContact(Long userId, CreateEmergencyContactRequest request);

    EmergencyContactResponse updateEmergencyContact(Long userId, Long contactId, UpdateEmergencyContactRequest request);

    void deleteEmergencyContact(Long userId, Long contactId);

    List<EmergencyContactResponse> getUserEmergencyContacts(Long userId);

}
