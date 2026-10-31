package org.example.match.community.repository;

import org.example.match.community.entity.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommunityMemberRepository
        extends JpaRepository<CommunityMember, UUID> {

    boolean existsByCommunityIdAndUserId(
            UUID communityId,
            UUID userId
    );

    Optional<CommunityMember> findByCommunityIdAndUserId(
            UUID communityId,
            UUID userId
    );
}