package com.messenger.e2ee.dto;

public record PreKeyBundleResponse(
        String userId,
        int registrationId,
        String identityPublicKey,
        int signedPreKeyId,
        String signedPreKeyPublic,
        String signedPreKeySignature,
        Integer preKeyId,
        String preKeyPublic
) {}
