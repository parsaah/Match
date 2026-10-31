package org.example.match.community.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.match.community.dto.CommunityCreateRequest;
import org.example.match.community.dto.CommunityResponse;
import org.example.match.community.dto.CommunityUpdateRequest;
import org.example.match.community.service.CommunityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<CommunityResponse> create(
            @Valid @RequestBody CommunityCreateRequest request,
            @RequestParam UUID creatorId
    ) {
        CommunityResponse response =
                communityService.create(request, creatorId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                communityService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getAll() {
        return ResponseEntity.ok(
                communityService.getAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CommunityUpdateRequest request
    ) {
        return ResponseEntity.ok(
                communityService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        communityService.delete(id);

        return ResponseEntity.noContent().build();
    }
}