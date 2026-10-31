package org.example.match.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.match.community.enums.CommunityMemberRole;
import org.example.match.community.enums.CommunityMemberStatus;
import org.example.match.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "community_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_community_member",
                        columnNames = {"community_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityMemberStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}

//import jakarta.persistence.*;
//import lombok.*;
//import org.example.match.community.enums.CommunityMemberRole;
//import org.example.match.community.enums.CommunityMemberStatus;
//import org.example.match.user.entity.User;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(
//        name = "community_members",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_community_member",
//                        columnNames = {"community_id", "user_id"}
//                )
//        }
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CommunityMember {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "community_id", nullable = false)
//    private Community community;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private CommunityMemberRole role;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private CommunityMemberStatus status;
//
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime joinedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        joinedAt = LocalDateTime.now();
//    }
//}