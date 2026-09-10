package com.ewsaly.ewsaly.service.emergency;

import com.ewsaly.ewsaly.dto.emergency.CreateEmergencyContactRequest;
import com.ewsaly.ewsaly.dto.emergency.EmergencyContactResponse;
import com.ewsaly.ewsaly.dto.emergency.UpdateEmergencyContactRequest;
import com.ewsaly.ewsaly.exceptions.EmergencyContactNotFoundException;
import com.ewsaly.ewsaly.mapper.EmergencyContactMapper;
import com.ewsaly.ewsaly.models.EmergencyContact;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.EmergencyContactRepository;
import com.ewsaly.ewsaly.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserService userService;
    private final EmergencyContactMapper emergencyContactMapper;

    @Override
    @Transactional
    public EmergencyContactResponse addEmergencyContact(Long userId, CreateEmergencyContactRequest request) {
        User user = userService.getUserById(userId);

        EmergencyContact contact = emergencyContactMapper.toEntity(request);
        contact.setUser(user);

        EmergencyContact savedContact = emergencyContactRepository.save(contact);

        return emergencyContactMapper.toResponse(savedContact);
    }

    @Override
    @Transactional
    public EmergencyContactResponse updateEmergencyContact(Long userId, Long contactId, UpdateEmergencyContactRequest request) {
        EmergencyContact contact = emergencyContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new EmergencyContactNotFoundException("error.resource_not_found"));

        emergencyContactMapper.updateEntityFromRequest(request, contact);

        EmergencyContact updatedContact = emergencyContactRepository.save(contact);
        return emergencyContactMapper.toResponse(updatedContact);
    }

    @Override
    @Transactional
    public void deleteEmergencyContact(Long userId, Long contactId) {
        User user = userService.getUserById(userId);

        EmergencyContact contact = emergencyContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new EmergencyContactNotFoundException("error.resource_not_found"));

        user.getEmergencyContacts().remove(contact);
        emergencyContactRepository.delete(contact);
    }

    @Override
    public List<EmergencyContactResponse> getUserEmergencyContacts(Long userId) {
        return emergencyContactMapper.toResponseList(
                emergencyContactRepository.findAllByUserId(userId)
        );
    }

}
