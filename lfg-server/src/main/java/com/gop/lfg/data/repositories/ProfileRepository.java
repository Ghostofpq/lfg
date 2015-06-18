package com.gop.lfg.data.repositories;

import com.gop.lfg.data.models.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component("ProfileRepository")
public interface ProfileRepository extends PagingAndSortingRepository<Profile, String> {
}
