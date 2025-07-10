package com.delivalue.tidings.domain.data.repository;


import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;

public interface MemberQueryRepository {
    long updateMemberProfile(ProfileUpdateRequest request);
}
