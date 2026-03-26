package com.messenger.chat;

import com.messenger.chat.dto.*;
import com.messenger.chat.entity.GroupRole;
import com.messenger.common.security.RequireGroupRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createGroup(
            Authentication auth,
            @Valid @RequestBody CreateGroupRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getGroup(
            Authentication auth,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(groupService.getGroup(id, userId));
    }

    @PatchMapping("/{id}")
    @RequireGroupRole(GroupRole.ADMIN)
    public ResponseEntity<ConversationResponse> updateGroup(
            Authentication auth,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(groupService.updateGroup(id, userId, request));
    }

    @DeleteMapping("/{id}")
    @RequireGroupRole(GroupRole.OWNER)
    public ResponseEntity<Void> deleteGroup(
            Authentication auth,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        groupService.deleteGroup(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ConversationResponse.GroupMemberInfo>> getMembers(
            Authentication auth,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(groupService.getMembers(id, userId));
    }

    @PostMapping("/{id}/members")
    @RequireGroupRole(GroupRole.ADMIN)
    public ResponseEntity<Map<String, String>> addMember(
            Authentication auth,
            @PathVariable UUID id,
            @Valid @RequestBody GroupMemberRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        groupService.addMember(id, userId, request.userId());
        return ResponseEntity.ok(Map.of("status", "member_added"));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @RequireGroupRole(GroupRole.ADMIN)
    public ResponseEntity<Void> removeMember(
            Authentication auth,
            @PathVariable UUID id,
            @PathVariable UUID memberId) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        groupService.removeMember(id, userId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/roles")
    @RequireGroupRole(GroupRole.OWNER)
    public ResponseEntity<Map<String, String>> changeRole(
            Authentication auth,
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        groupService.changeRole(id, userId, request);
        return ResponseEntity.ok(Map.of("status", "role_changed"));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveGroup(
            Authentication auth,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        groupService.leaveGroup(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join/{inviteLink}")
    public ResponseEntity<ConversationResponse> joinByInviteLink(
            Authentication auth,
            @PathVariable String inviteLink) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(groupService.joinByInviteLink(inviteLink, userId));
    }
}
