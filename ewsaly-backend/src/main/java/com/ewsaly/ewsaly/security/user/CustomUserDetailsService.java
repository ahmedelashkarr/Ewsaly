package com.ewsaly.ewsaly.security.user;

import com.ewsaly.ewsaly.exceptions.PhoneNumberNotFoundException;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new PhoneNumberNotFoundException(username));

        return new CustomUserDetails(user);
    }
}
