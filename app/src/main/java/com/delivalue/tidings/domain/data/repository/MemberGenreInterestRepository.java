package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberGenreInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberGenreInterestRepository extends JpaRepository<MemberGenreInterest, Long> {

    List<MemberGenreInterest> findAllByMemberIdOrderByInterestScoreDesc(String memberId);
}
