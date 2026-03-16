package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.LowSignalUserProfile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LowSignalUserProfileRepository extends MongoRepository<LowSignalUserProfile, ObjectId> {

    Optional<LowSignalUserProfile> findByMemberId(String memberId);

    boolean existsByMemberId(String memberId);
}
