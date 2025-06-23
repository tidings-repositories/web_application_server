package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByPublicId(String publicId);
}
