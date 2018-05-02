package com.shaw.dolores.service;

import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User updateOrSave(User visitor) {
        if (visitor != null) {
            User user = userRepository.findUserByAccountAndOauthFrom(visitor.getAccount(), visitor.getOauthFrom());
            if (user != null) {
                visitor.setId(user.getId());
                if (!Objects.equals(user.getMoreInfo(), visitor.getMoreInfo())) {
                    visitor.setId(user.getId());
                    userRepository.save(visitor);
                }
            } else {
                userRepository.save(visitor);
            }
            return visitor;
        }
        return null;
    }
}
