package com.ewsaly.ewsaly.service.auth;

import com.ewsaly.ewsaly.dto.auth.AuthResponse;
import com.ewsaly.ewsaly.dto.auth.LoginRequest;
import com.ewsaly.ewsaly.dto.auth.ResendOtpRequest;
import com.ewsaly.ewsaly.dto.auth.SignUpRequest;
import com.ewsaly.ewsaly.dto.auth.VerifyOtpRequest;
import com.ewsaly.ewsaly.exceptions.*;
import com.ewsaly.ewsaly.models.User;
import com.ewsaly.ewsaly.repository.UserRepository;
import com.ewsaly.ewsaly.security.jwt.JwtUtils;
import com.ewsaly.ewsaly.service.otp.OtpVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpVerificationService otpVerificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final com.ewsaly.ewsaly.mapper.UserMapper userMapper;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
        );

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberNotFoundException(request.getPhoneNumber()));


        if (!user.getIsVerified()) {
            throw new UserNotVerifiedException("error.auth.user.not.verified");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateTokenForUser(user);
        return new AuthResponse(null, token, user.getIsProfileCompleted() ,LocalDateTime.now());
    }


    @Override
    @Transactional
    public void register(SignUpRequest request) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            if (existingUser.getIsVerified()) {
                throw new PhoneNumberAlreadyExistsException("error.auth.phone.duplicate");
            }

            // update new user data
            userMapper.updateEntityFromRequest(request, existingUser);
            existingUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            userRepository.save(existingUser);

            String otpCode = generateOtpCode();
            otpVerificationService.createOtp(request.getPhoneNumber(), otpCode);
            return;
        }

        // Create user (not yet verified)
        User newUser = userMapper.toEntity(request);
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        String otpCode = generateOtpCode();
        otpVerificationService.createOtp(request.getPhoneNumber(), otpCode);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberNotFoundException("error.auth.phone.not.found"));

        if (user.getIsVerified()) {
            throw new AccountAlreadyVerifiedException("error.auth.account.already.verified");
        }

        boolean isValid = otpVerificationService.verifyOtp(request.getPhoneNumber(), request.getOtpCode());

        if (!isValid) {
            throw new InvalidOtpException("error.auth.otp.invalid");
        }

        user.setIsVerified(true);

        String token = jwtUtils.generateTokenForUser(user);
        return new AuthResponse(null, token, user.getIsProfileCompleted() ,LocalDateTime.now());
    }

    @Override
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberNotFoundException("error.auth.phone.not.found"));

        if (user.getIsVerified()) {
            throw new AccountAlreadyVerifiedException("error.auth.account.already.verified");
        }

        String otpCode = generateOtpCode();
        otpVerificationService.createOtp(request.getPhoneNumber(), otpCode);
    }


    private String generateOtpCode() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }


}
