# FS

---

# Description

FS (File System) is a micro-service for handling files. It serves both as an interface for S3 storage and SSM.

Each and every file uploaded to this service are saved within S3. Optionally, it is possible to hash and send some of those files into an SSM.

# Architecture

The communication with the S3 storage is done via a [MinIO](https://docs.min.io/docs/java-client-quickstart-guide.html) SDK, which can communicate with any S3-compatible API.

The communication with the SSM is done with an event-sourcing S2 automate and uses Redis as a cache.

# Getting Started

FS is designed to be deployed as a standalone micro-service.
A docker image can be found [here](https://hub.docker.com/repository/docker/komune-io/fs-gateway) and used in a docker-compose:

```yaml
version: "3.7"
services:
  fs-gateway:
    container_name: fs-gateway
    image: komune-io/fs-gateway:${VERSION}
    environment:
      - fs_s3_internal-url=${INTERNAL_URL}
      - fs_s3_external-url=${EXTERNAL_URL}
      - fs_s3_bucket=${BUCKET}
      - fs_s3_password=${MINIO_PASS}
    ports:
      - 8090:8090
```

Once it is up and running, you can start making requests via HTTP.

⚠️ **To run FS, you need to have a reachable MinIO instance, and a Redis and a SSM if you want to use S2.**

If you are in a java environment, all the necessary models and commands can be found [here](https://mvnrepository.com/artifact/io.komune.fs/file-domain).
An SDK that sugarcoats the http requests is also available [here](https://mvnrepository.com/artifact/io.komune.fs/file-client).

```kotlin
implementation("io.komune.fs:file-domain:${Versions.fs}")
implementation("io.komune.fs:file-client:${Versions.fs}")
```

The client provided in the SDK uses Ktor under the hood and should be a singleton in your application in order to prevent any memory leak. Ex:

```kotlin
@Configuration
class FsConfig {

    @Value("\${platform.fs.url}")
    lateinit var fsUrl: String

    @Bean
    fun fsClient() = FileClient(
        url = fsUrl
    )
}
```

# Endpoints & Roles

| Endpoints | Associated role |
| --- | --- |
| fileGet | fs_file_read |
| fileList | fs_file_read |
| fileUpload | fs_file_write |
| fileDelete | fs_file_write |
| initPublicDirectory | fs_policy_write |
| revokePublicDirectory | fs_policy_write |

| Roles | Endpoints |
| --- | --- |
| fs_file_read | fileGet, fileList |
| fs_file_write | fileUpload, fileDelete |
| fs_policy_write | initPublicDirectory, revokePublicDirectory |

# Configuration

## S3

Properties prefix: `fs.s3`

| Property | Description                                                                                                                                                                                            | Example              | Default |
| --- |--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------| --- |
| internal-url | URL used by FS to communicate with the S3 server                                                                                                                                                       | http://minio:9000    | http://localhost:9000 |
| external-url | URL used to build usable file URLs                                                                                                                                                                     | https://s3.komune.io | http://localhost:9000 |
| region | Name of the location of the server                                                                                                                                                                     | sbg                  | us-west-1 |
| username | Login to S3                                                                                                                                                                                            | johndeuf             | minio |
| password | Login to S3                                                                                                                                                                                            | smartpassword        | minio123 |
| bucket | S3 bucket in which the files will be uploaded                                                                                                                                                          | bananas              | fs-sample |
| dns | Enable or disable virtual style endpoint i.e. if the bucket name is respectively part of : <br/> - the url domain (ex: https://bucket.s3.komune.io) - the url path (ex: https://s3.komune.io/bucket)            | true                 | false |

## Blockchain

> The configurations below are optional. If you don’t intend to save any of your files into a blockchain, you can skip this section.
>

### SSM

Properties prefix: `ssm.couchdb`

| Property | Description | Example | Default |
| --- | --- | --- | --- |
| url | URL of the CouchDB associated with the SSM | http://localhost:5984 |  |
| username | CouchDB login | couchdb |  |
| password | CouchDB login | couchdb |  |
| serviceName |  | fs |  |

Properties prefix: `ssm.chaincode`

| Property | Description | Example | Default |
| --- | --- | --- | --- |
| url | URL of a peer | http://localhost:9090 |  |

Properties prefix: `fs.ssm`

| Property | Description | Example | Default |
| --- | --- | --- | --- |
| channel | Channel of the SSM to send to files to | stuff | sandbox |
| chaincode | Chaincode of the SSM to send to files to | fs | ssm |
| signerName | Name of the SSM user signing the transactions |  | ssm-admin |
| signerKey | Path to a file containing the key of the signer |  | user/ssm-admin |

### Redis

Properties prefix: `spring.redis`

| Property | Description | Example | Default |
| --- | --- | --- | --- |
| database | Index of the database to use | 3 | 0 |
| host | Redis host | redis | localhost |
| port | Redis port | 6379 | 6379 |
| password | Redis login password | blblbl | sOmE_sEcUrE_pAsS |

# Errors

## fs-script quickstart

See docs/task.md for detailed configuration and usage.

Run locally (no web server):
- Create application.yml with fs.script.init.* and fs.script.retry.* as documented.
- Run: ./gradlew :fs-script:fs-script-gateway:bootRun --args=""

Docker image targets:
- make docker-fs-script-build
- make docker-fs-script-publish
- make docker-fs-script-promote

## Migrating from MinIO to RustFS

The MinIO server is no longer published. The `fs-script` image can copy an existing MinIO
server into a [RustFS](https://github.com/rustfs/rustfs) one (or between any two S3-compatible
servers). It runs instead of the import script when `fs.script.migrate.enabled` is true.

What is copied, bucket by bucket:
- every object, with its content type and user metadata (`id`, `vectorized`, ...);
- the bucket policy, which holds the public directories created by `initPublicDirectory`.

Users and access keys are not copied: give RustFS the credentials FS already uses.

| Property (`fs.script.migrate.*`) | Environment variable | Description | Default |
| --- | --- | --- | --- |
| enabled | FS_SCRIPT_MIGRATE_ENABLED | Run the migration instead of the import | false |
| dry-run | FS_SCRIPT_MIGRATE_DRY_RUN | Log what would be copied or deleted, write nothing | false |
| buckets | FS_SCRIPT_MIGRATE_BUCKETS | Comma-separated buckets to migrate, empty for all of them | |
| concurrency | FS_SCRIPT_MIGRATE_CONCURRENCY | Objects copied in parallel | 8 |
| delete-extraneous | FS_SCRIPT_MIGRATE_DELETE_EXTRANEOUS | Delete target objects that no longer exist on the source | false |
| source.internal-url | FS_MIGRATE_SOURCE_URL | URL of the MinIO server | http://localhost:9000 |
| source.username | FS_MIGRATE_SOURCE_USERNAME | Login to MinIO | minio |
| source.password | FS_MIGRATE_SOURCE_PASSWORD | Password of MinIO | minio123 |

The target is the server configured under `fs.script.s3` (`FS_S3_INTERNAL_URL`, `FS_S3_USERNAME`,
`FS_S3_PASSWORD`).

Objects already on the target with the same size, content type and metadata are skipped, so the
script can be run as many times as needed. A typical cutover:
1. Start RustFS next to MinIO with the same credentials, and run the migration while FS is live.
2. Stop writes to FS, then run it again with `delete-extraneous` to copy the last changes and
   drop files deleted in the meantime.
3. Point `fs.s3.internal-url` at RustFS, and point the host of `fs.s3.external-url` at it too:
   file URLs recorded in the SSM keep that host.

In the dev environment:
```bash
docker compose --env-file infra/docker-compose/.env_dev -f infra/docker-compose/docker-compose-rustfs.yml up -d
docker compose --env-file infra/docker-compose/.env_dev -f infra/docker-compose/docker-compose-fs-migrate.yml up
```
