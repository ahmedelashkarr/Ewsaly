package com.ewsaly.ewsaly.service.user;

import com.ewsaly.ewsaly.models.User;

public interface UserService {

    User getUserById(Long userId);

    User saveUser(User user);
}

