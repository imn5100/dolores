package com.shaw.dolores.dao;

import com.shaw.dolores.bo.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User findUserByAccountAndOauthFrom(String account, int oauthFrom);
}
