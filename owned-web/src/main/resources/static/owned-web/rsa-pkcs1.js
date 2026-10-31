/* Owned RSAES-PKCS1-v1_5 encryption for the existing X.509/base64 login wire.
 * Uses browser CSPRNG and BigInt. RFC 8017, sections 7.2.1 and B.1 describe
 * the encoded message; this file is original project code, not vendor JS. */
(function (global) {
  'use strict';
  function bytes64(text) { return Uint8Array.from(atob(text), function (c) { return c.charCodeAt(0); }); }
  function integer(bytes) {
    var value = 0n;
    for (var i = 0; i < bytes.length; i++) value = (value << 8n) | BigInt(bytes[i]);
    return value;
  }
  function reader(bytes) {
    var offset = 0;
    return function (tag) {
      if (offset >= bytes.length || bytes[offset++] !== tag) throw new Error('无法解析 RSA 公钥');
      var length = bytes[offset++];
      if (length & 128) {
        var count = length & 127;
        if (!count || count > 4 || offset + count > bytes.length) throw new Error('无效的 RSA 公钥长度');
        length = 0;
        for (var i = 0; i < count; i++) length = length * 256 + bytes[offset++];
      }
      if (offset + length > bytes.length) throw new Error('截断的 RSA 公钥');
      var result = bytes.slice(offset, offset + length); offset += length; return result;
    };
  }
  function publicKey(text) {
    var spki = reader(reader(bytes64(text))(48));
    var algorithm = spki(48);
    var expected = [6,9,42,134,72,134,247,13,1,1,1,5,0];
    if (algorithm.length !== expected.length || expected.some(function (v, i) { return algorithm[i] !== v; })) throw new Error('不支持的公钥算法');
    var bits = spki(3);
    if (bits[0] !== 0) throw new Error('无效的 RSA 公钥位串');
    var rsa = reader(reader(bits.slice(1))(48));
    var modulus = rsa(2), exponent = rsa(2);
    while (modulus.length > 1 && modulus[0] === 0) modulus = modulus.slice(1);
    if (modulus.length < 128 || exponent.length > 8) throw new Error('不支持的 RSA 公钥参数');
    return { n: integer(modulus), e: integer(exponent), size: modulus.length };
  }
  function modPow(base, exponent, modulus) {
    var result = 1n;
    while (exponent > 0n) {
      if (exponent & 1n) result = result * base % modulus;
      exponent >>= 1n; base = base * base % modulus;
    }
    return result;
  }
  function encrypt(password, keyText) {
    if (!global.crypto || !global.crypto.getRandomValues || typeof BigInt !== 'function' || typeof TextEncoder !== 'function') throw new Error('浏览器不支持安全的 RSA 密码传输');
    var key = publicKey(keyText), message = new TextEncoder().encode(password);
    if (message.length > key.size - 11) throw new Error('密码超过当前 RSA 传输长度');
    var block = new Uint8Array(key.size); block[1] = 2;
    var delimiter = key.size - message.length - 1, random = new Uint8Array(1);
    for (var i = 2; i < delimiter; i++) {
      do { global.crypto.getRandomValues(random); } while (random[0] === 0);
      block[i] = random[0];
    }
    block.set(message, delimiter + 1);
    var encrypted = modPow(integer(block), key.e, key.n), output = new Uint8Array(key.size);
    for (var j = output.length - 1; j >= 0; j--) { output[j] = Number(encrypted & 255n); encrypted >>= 8n; }
    message.fill(0); block.fill(0); random.fill(0);
    var text = ''; output.forEach(function (b) { text += String.fromCharCode(b); });
    return btoa(text);
  }
  global.OwnedRsa = Object.freeze({ encrypt: encrypt });
})(window);
