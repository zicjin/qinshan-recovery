package com.sunwayworld.owned.attachments.replay;
/** Only an explicitly configured source-labelled replay transport is accepted. */
public interface OwnedReplayTransport {
    OwnedReplayResponse post(String path, String merchantId, String fileSign, String idempotencyKey) throws Exception;
}
