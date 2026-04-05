package com.messenger.e2ee;

import com.messenger.e2ee.dto.ConsumedRequest;
import com.messenger.e2ee.dto.DistributeSenderKeysRequest;
import com.messenger.e2ee.dto.PendingSenderKeyResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/sender-keys")
public class GroupSenderKeyController {

    private final GroupSenderKeyService service;

    public GroupSenderKeyController(GroupSenderKeyService service) {
        this.service = service;
    }

    @PostMapping("/distribute")
    public ResponseEntity<Void> distribute(Principal principal,
                                           @Valid @RequestBody DistributeSenderKeysRequest request) {
        UUID userId = UUID.fromString(principal.getName());
        service.distribute(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PendingSenderKeyResponse>> getPending(Principal principal,
                                                                      @RequestParam(required = false) UUID groupId) {
        UUID userId = UUID.fromString(principal.getName());
        if (groupId != null) {
            return ResponseEntity.ok(service.getPendingForGroup(userId, groupId));
        }
        return ResponseEntity.ok(service.getPending(userId));
    }

    @PostMapping("/consumed")
    public ResponseEntity<Void> markConsumed(Principal principal,
                                             @Valid @RequestBody ConsumedRequest request) {
        UUID userId = UUID.fromString(principal.getName());
        service.markConsumed(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteForGroup(Principal principal,
                                               @PathVariable UUID groupId) {
        UUID userId = UUID.fromString(principal.getName());
        service.deleteForUser(groupId, userId);
        return ResponseEntity.ok().build();
    }
}
