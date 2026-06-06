# connect-fs — Context

A file-storage microservice. Uploads land in an S3-compatible object store (MinIO); each file's lifecycle is modelled as an [S2 Automate](../../fixers/fixers-s2/CONTEXT.md#automate). Optionally vectorises file contents into a Knowledge Base for downstream search / RAG. Redis caches hot reads.

## Glossary

### File

The aggregate. Identified by a `FileId` (string). Carries: storage `path`, public `url`, `metadata`, `size`, `isDirectory`, `vectorized` flag, `lastModificationDate`. Two views:

- **`File`** — the DTO returned over HTTP.
- **`FileEntity`** — the JPA-persisted row (id, name, objectType, objectId, directory, metadata, uploadDate, status).

### FileState

The two states of the FileState Automate:

- **`Exists`** — file is in the store (current binary content lives in MinIO).
- **`Deleted`** — file has been retired; the row is kept for audit but the storage object may have been removed.

### File lifecycle (Automate)

```
                FileInitCommand
                      │
                      ▼
       ┌──────────► Exists ─── FileLogCommand (self) ──┐
       │              │                                │
       │              │ FileDeleteByIdCommand          │
       │              ▼                                │
       │           Deleted                             │
       └───────────────────────────────────────────────┘
```

Events: `FileInitiatedEvent`, `FileLoggedEvent`, `FileDeletedEvent`. All transitions are gated by the single role [Tracer](#filerole-tracer).

### FileRole.Tracer

The sole `S2Role` permitted on every transition. Reading: *"the actor whose action is being traced into the file's audit log."* The FS Automate intentionally does **not** distinguish writer / deleter / reader at the role level — finer-grained access control is enforced upstream by [connect-im](../connect-im/CONTEXT.md) and the API gateway, not by the S2 role check.

### Knowledge Base (KB)

An optional, separate service that consumes file contents and produces vector embeddings for similarity search / RAG. `fs-infra` ships a thin client (`fs-kb-client`) that posts a File to the KB; on success the File's `vectorized` flag flips to `true`. KB itself is not part of this repo.

### Storage backend

S3-compatible object store, in practice MinIO. The MinIO client lives in `fs-infra/`. The Automate's `Exists` state asserts the storage object is reachable; FS does *not* persist the binary content itself.

### No on-chain integration

FS does **not** consume [fixers-c2](../../fixers/fixers-c2/CONTEXT.md). Files are not hashed onto a Hyperledger Fabric SSM. Any prior mention of "optional SSM hashing" is incorrect.

## Module map

- `fs-api/` — Spring Boot HTTP gateway exposing F2 functions.
- `fs-s2/` — File Automate definition + sourcing.
- `fs-spring/` — Spring exception handling utilities.
- `fs-infra/` — MinIO S3 client + KB client.
- `fs-commons/` — Jackson / serialization utilities.
- `fs-script/` — file import + autoconfig.

## Published artifacts

Maven group `io.komune.fs`:

- `file-domain` (multiplatform DTO + serialization)
- `file-client` (Ktor HTTP client wrapper)
- `fs-spring-utils`
- `fs-kb-client`

## Cross-references

- State machine vocabulary inherited from [fixers-s2](../../fixers/fixers-s2/CONTEXT.md): Automate, S2State, S2Command, S2Event, S2Role.
- F2 function exposure & client from [fixers-f2](../../fixers/fixers-f2/CONTEXT.md).
- Consumed by [connect-im](../connect-im/CONTEXT.md) (via `fs-file-client`) for user / org profile attachments.
- UI binding: [g2-fs](../../fixers/fixers-g2/CONTEXT.md#ui-binding).
- Layer position: [../../docs/adr/0001-submodule-dependency-layers.md](../../docs/adr/0001-submodule-dependency-layers.md).
