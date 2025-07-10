package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.QMember;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public long updateMemberProfile(ProfileUpdateRequest request) {
        boolean hasUpdate = false;
        QMember qMember = QMember.member;

        JPAUpdateClause update = queryFactory.update(qMember);

        if(request.getUserName() != null) {
            update.set(qMember.name, request.getUserName());
            hasUpdate = true;
        }

        if(request.getProfileImage() != null) {
            update.set(qMember.profileImage, request.getProfileImage());
            hasUpdate = true;
        }

        if(request.getBio() != null) {
            update.set(qMember.bio, request.getBio());
            hasUpdate = true;
        }

        if(request.getBadgeId() != null) {
            if(request.getBadgeId() == 0) update.set(qMember.badge.id, (Integer) null);
            else update.set(qMember.badge.id, request.getBadgeId());
            hasUpdate = true;
        }

        return hasUpdate ? update.where(qMember.id.eq(request.getId())).execute() : 0;
    }
}
