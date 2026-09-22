# AGENTS.md — android/keymanager/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

Wraps the **Android Keystore** (hardware/software-backed, via
`android.security.keystore.KeyGenParameterSpec`/`KeyProperties`,
`KeyStore.getInstance("AndroidKeyStore")`) combined with BouncyCastle
(RSA/OAEP, SHA-256/SHA-1, GCM) and `jose4j` (JWS signing/verification)
to provide client-side signing, encryption, and JWT/JWS verification for
the registration client. This is not a generic crypto helper — it's
specifically the on-device key management layer.

## Layout

```text
keymanager/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/io/mosip/registration/keymanager/
    │   │   ├── dao/, dto/, entity/, exception/, repository/, util/
    │   │   └── service/, spi/                # see below
    │   └── assets/config.properties
    └── test/java/...                          # 12 unit test files
```

No `src/androidTest`, no module-level README.

## `spi/` — what this module exposes

- **`ClientCryptoManagerService`** — the main contract:
  `sign(SignRequestDto)`, `verifySign(SignVerifyRequestDto)`,
  `encrypt(CryptoRequestDto)`, `decrypt(CryptoRequestDto)`,
  `getPublicKey(PublicKeyRequestDto)`, `getMachineName()`,
  `getClientKeyIndex()`, `getMachineDetails()`,
  `jwtVerify(JWTSignatureVerifyRequestDto)`. Implemented by
  `service/LocalClientCryptoServiceImpl.java`, which is the class that
  actually touches `AndroidKeyStore`/BouncyCastle/`jose4j`.
- **`CertificateManagerService`** — CA certificate store operations
  (`service/CertificateManagerServiceImpl.java` +
  `service/CertificateDBHelper.java`).
- **`CryptoManagerService`** — implemented by
  `service/CryptoManagerServiceImpl.java`.

No Pigeon references in this module — `HostApi` impls live in
`android/app`'s `api_services/` (see `../AGENTS.md`).

## Build & Test Commands

```bash
cd android
./gradlew :keymanager:assembleDebug
./gradlew :keymanager:testDebugUnitTest
```

Applies the shared `../jacoco.gradle` coverage config. Note:
`testImplementation project(':clientmanager')` — this module's **tests**
(not its main code) depend on `clientmanager`, the reverse of the
production dependency direction (`clientmanager` depends on
`keymanager`). Don't be confused by this when tracing dependencies —
it's test-only.

## Configuration

Notable dependencies beyond the shared root-`ext` stack: BouncyCastle
(`bcprov-jdk18on`, `bcpkix-jdk18on`), `org.bitbucket.b_c:jose4j`,
Jackson + `jackson-datatype-jsr310`, `androidx.room:room-common`,
`commons-lang3`/`commons-codec`/`commons-io`. No `project(...)` module
dependencies in main code — this is a leaf module other managers depend
on, not the other way around.

## Agent rules

### Do

1. Keep all Android Keystore interaction inside
   `LocalClientCryptoServiceImpl` (or a similarly-scoped class under
   `service/`) — don't scatter `KeyStore`/`KeyGenParameterSpec` calls
   across other modules.
2. Remember `clientmanager` and `packetmanager` both depend on this
   module's `spi` interfaces for crypto operations — a breaking change
   to `ClientCryptoManagerService`'s signature ripples into both.

### Do not

1. Do not add Pigeon imports/interfaces to this module.
2. Do not assume the `testImplementation project(':clientmanager')` line
   means this module depends on `clientmanager` in production — it's
   test-scope only.
3. Do not hand-roll a second crypto/keystore path elsewhere in the app —
   route new signing/encryption needs through this module's `spi`.
