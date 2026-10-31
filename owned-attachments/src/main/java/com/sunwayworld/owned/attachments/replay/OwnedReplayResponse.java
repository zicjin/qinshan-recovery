package com.sunwayworld.owned.attachments.replay;
/** Source-labelled replay transport result; response bytes are bounded by the service. */
public final class OwnedReplayResponse {
    private final int status;
    private final byte[] body;
    public OwnedReplayResponse(int status, byte[] body) {
        this.status = status;
        this.body = body == null ? new byte[0] : body.clone();
    }
    public int getStatus() { return status; }
    public byte[] getBody() { return body.clone(); }
}
