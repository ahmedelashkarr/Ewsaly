package com.ewsaly.ewsaly.service.user;

import com.ewsaly.ewsaly.exceptions.PhoneNumberNotFoundException;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PhoneNumberNotFoundException("error.auth.phone.not.found"));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
