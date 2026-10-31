package com.sunwayworld.owned.internal.web;

/**
 * Decodes the transport credential used by the login wire contract.  The
 * supplier page currently sends an RSA-encrypted {@code password} with its
 * {@code SECURITY-RSA-KEY} header; the owned boundary deliberately requires a
 * separately supplied decoder instead of accepting plaintext or inventing a
 * key service.
 */
public interface OwnedLoginCredentialDecoder {
    char[] decode(String userId, String encodedPassword, String transportKey);
}
