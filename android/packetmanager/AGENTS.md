# AGENTS.md — android/packetmanager/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

Builds and persists MOSIP registration packets: setting fields,
attaching biometrics/documents, CBEFF biometric-record XML encoding,
packet signing/encryption, and packet storage. Depends on `keymanager`
for the actual crypto operations.

## Layout

```text
packetmanager/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/io/mosip/registration/packetmanager/
    │   │   ├── cbeffutil/jaxbclasses/     # CBEFF biometric-record XML/JAXB model (BIR, BDBInfo, BiometricTypeTransformer, ...)
    │   │   ├── dto/                        # + dto/PacketWriter subpackage: BiometricRecord, Packet, PacketInfo, RegistrationPacket, Document, ...
    │   │   ├── exception/, util/
    │   │   └── service/, spi/               # see below
    │   └── assets/packetmanagerconfig.properties
    └── test/
        ├── java/...                          # 10 unit test files
        └── assets/*.zip                        # sample packet fixtures (packet.zip, obj.zip, cont3.zip, etc.)
```

**Known repo quirk**: `packetmanager/acc5/cont5/src/proc/refId.meta` is a
stray file committed outside `src/` — it looks like leftover output from
a local test/packet-extraction run, not source code. Don't treat it as
intentional structure, and don't add more files under `acc5/` — if
you're cleaning up the repo, flag this to a maintainer rather than
silently deleting it (it may be relied on by something not obvious from
the module alone).

No module-level README.

## `spi/` — what this module exposes

- **`PacketWriterService`** — the actual "build/persist a packet" API:
  `setField(id, fieldId, value)`,
  `setBiometric(id, fieldId, BiometricRecord)`,
  `setDocument(id, fieldId, Document)`,
  `addMetaInfo(id, key, value)`, `addAudits(id, List<Map>)`,
  `addAudit(id, Map)`,
  `persistPacket(id, version, schemaJson, source, process, offlineMode, refId)`.
  Implemented by `service/PacketWriterServiceImpl.java`.
- **`IPacketCryptoService`** — `sign(byte[] packet)`,
  `encrypt(String refId, byte[] packet)`. Implemented by
  `service/PacketCryptoServiceImpl.java`. This is the interface
  `clientmanager`'s `PacketServiceImpl` consumes directly — see
  `../clientmanager/AGENTS.md`.
- **`ObjectAdapterService`** — filesystem/object-store adapter for
  packet storage (POSIX-style), implemented by
  `service/PosixAdapterServiceImpl.java`, used with
  `util/PacketKeeper.java`, `util/ObjectStoreUtil.java`,
  `util/StorageUtils.java`.

No Pigeon references in this module — `HostApi` impls live in
`android/app`'s `api_services/` (see `../AGENTS.md`).

## Build & Test Commands

```bash
cd android
./gradlew :packetmanager:assembleDebug
./gradlew :packetmanager:testDebugUnitTest
```

Applies the shared `../jacoco.gradle` coverage config.

## Configuration

Notable dependencies beyond the shared root-`ext` stack:
`org.simpleframework:simple-xml` (CBEFF JAXB-style XML, with
`stax`/`xpp3` excluded), Jackson databind, `commons-io`/`commons-lang3`.
Depends on `project(':keymanager')` for crypto — see
`../keymanager/AGENTS.md`.

## Agent rules

### Do

1. Route packet signing/encryption through `IPacketCryptoService`
   (backed by `keymanager`) — don't reimplement crypto here.
2. Use the `test/assets/*.zip` sample packets as fixtures when writing
   new tests for packet reading/writing logic.

### Do not

1. Do not add Pigeon imports/interfaces to this module.
2. Do not treat `acc5/cont5/src/proc/refId.meta` as a template for
   committing generated/test output — it's a known stray artifact, not
   an intentional pattern.
