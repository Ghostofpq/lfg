package com.gop.lfg.data.repositories;

import com.gop.lfg.data.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component("UserRepository")
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    User findByLogin(final String login);

    User findByEmail(final String email);
}
