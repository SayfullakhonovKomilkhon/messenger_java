package com.messenger.e2ee;

import com.messenger.e2ee.dto.ConsumedRequest;
import com.messenger.e2ee.dto.DistributeSenderKeysRequest;
import com.messenger.e2ee.dto.PendingSenderKeyResponse;
import com.messenger.e2ee.entity.GroupSenderKeyEntity;
import com.messenger.e2ee.repository.GroupSenderKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupSenderKeyService {

    private static final Logger log = LoggerFactory.getLogger(GroupSenderKeyService.class);

    private final GroupSenderKeyRepository repository;

    public GroupSenderKeyService(GroupSenderKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void distribute(UUID senderId, DistributeSenderKeysRequest request) {
        repository.deleteBySenderInGroup(request.groupId(), senderId);

        for (var dist : request.distributions()) {
            var entity = new GroupSenderKeyEntity();
            entity.setGroupId(request.groupId());
            entity.setSenderId(senderId);
            entity.setRecipientId(dist.recipientId());
            entity.setDistributionMessage(dist.encryptedSkdm());
            entity.setConsumed(false);
            repository.save(entity);
        }
        log.info("Distributed sender keys for group {} from {} to {} recipients",
                request.groupId(), senderId, request.distributions().size());
    }

    public List<PendingSenderKeyResponse> getPending(UUID userId) {
        return repository.findByRecipientIdAndConsumedFalse(userId).stream()
                .map(e -> new PendingSenderKeyResponse(e.getGroupId(), e.getSenderId(), e.getDistributionMessage()))
                .toList();
    }

    public List<PendingSenderKeyResponse> getPendingForGroup(UUID userId, UUID groupId) {
        return repository.findByGroupIdAndRecipientIdAndConsumedFalse(groupId, userId).stream()
                .map(e -> new PendingSenderKeyResponse(e.getGroupId(), e.getSenderId(), e.getDistributionMessage()))
                .toList();
    }

    @Transactional
    public void markConsumed(UUID userId, ConsumedRequest request) {
        repository.markConsumed(userId, request.groupId(), request.senderId());
        log.debug("Marked sender key consumed: recipient={}, group={}, sender={}",
                userId, request.groupId(), request.senderId());
    }

    @Transactional
    public void deleteForUser(UUID groupId, UUID userId) {
        repository.deleteBySenderInGroup(groupId, userId);
        repository.deleteByRecipientInGroup(groupId, userId);
        log.info("Deleted sender keys for user {} in group {}", userId, groupId);
    }

    @Transactional
    public void deleteAllForGroup(UUID groupId) {
        repository.deleteAllByGroupId(groupId);
        log.info("Deleted all sender keys for group {}", groupId);
    }
}
