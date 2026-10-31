package org.example.match.community.service;

import lombok.RequiredArgsConstructor;
import org.example.match.community.dto.CommunityCreateRequest;
import org.example.match.community.dto.CommunityResponse;
import org.example.match.community.dto.CommunityUpdateRequest;
import org.example.match.community.entity.Category;
import org.example.match.community.entity.Community;
import org.example.match.community.entity.CommunityMember;
import org.example.match.community.exception.CategoryNotFoundException;
import org.example.match.user.entity.User;
import org.example.match.community.enums.CommunityMemberRole;
import org.example.match.community.enums.CommunityMemberStatus;
import org.example.match.community.exception.CommunityNotFoundException;
import org.example.match.community.mapper.CommunityMapper;
import org.example.match.community.repository.CategoryRepository;
import org.example.match.community.repository.CommunityMemberRepository;
import org.example.match.community.repository.CommunityRepository;
import org.example.match.user.exception.UserNotFoundException;
import org.example.match.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CommunityMapper communityMapper;

    public CommunityResponse create(
            CommunityCreateRequest request,
            UUID creatorId
    ) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.getCategoryId())
                );

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() ->
                        new UserNotFoundException(creatorId)
                );

        Community community = communityMapper.toEntity(request);

        community.setCategory(category);

        Community savedCommunity = communityRepository.save(community);

        CommunityMember owner = new CommunityMember();

        owner.setCommunity(savedCommunity);
        owner.setUser(creator);
        owner.setRole(CommunityMemberRole.OWNER);
        owner.setStatus(CommunityMemberStatus.ACTIVE);

        communityMemberRepository.save(owner);

        return communityMapper.toResponse(savedCommunity);
    }

    @Transactional(readOnly = true)
    public CommunityResponse getById(UUID id) {

        Community community = communityRepository.findById(id)
                .orElseThrow(() ->
                        new CommunityNotFoundException(id)
                );

        return communityMapper.toResponse(community);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getAll() {

        return communityRepository.findAll()
                .stream()
                .map(communityMapper::toResponse)
                .toList();
    }

    public CommunityResponse update(
            UUID id,
            CommunityUpdateRequest request
    ) {

        Community community = communityRepository.findById(id)
                .orElseThrow(() ->
                        new CommunityNotFoundException(id)
                );

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.getCategoryId())
                );


        communityMapper.updateEntity(request, community);

        community.setCategory(category);

        return communityMapper.toResponse(community);
    }

    public void delete(UUID id) {

        Community community = communityRepository.findById(id)
                .orElseThrow(() ->
                        new CommunityNotFoundException(id)
                );

        communityRepository.delete(community);
    }
}