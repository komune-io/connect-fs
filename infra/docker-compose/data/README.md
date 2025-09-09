# Multi-Bucket Import Structure

This directory demonstrates the fs-script multi-bucket import system where each subdirectory represents a separate S3 bucket with independent policies and configurations.

## Directory Structure

```
data/
├── documents/          # Business documents bucket
├── images/             # Web assets and images bucket  
├── media/              # Video and audio content bucket
└── test-import/        # Test files bucket
```

## Bucket Configurations

### 📋 documents/ → `documents` S3 bucket
**Use Case**: Business documents, contracts, legal files
- **Retention**: 7 years (2555 days), 10 versions
- **Access**: Private with company intranet access only
- **Security**: Encryption enabled, no compression
- **Metadata**: Business classification, department tagging, compliance markers

**Example files**:
- `contract.pdf` - Sample business contract
- `legal/privacy-policy.md` - Privacy policy document

### 🎨 images/ → `images` S3 bucket  
**Use Case**: Website assets, logos, public images
- **Retention**: 1 year (365 days), 3 versions
- **Access**: Public with CORS enabled for all origins
- **Optimization**: Compression enabled for faster delivery
- **Metadata**: Asset type, web optimization flags, caching directives

**Example files**:
- `logo.svg` - Corporate logo with brand metadata
- `photos/team.jpg` - Team photo

### 🎬 media/ → `media` S3 bucket
**Use Case**: Video and audio content for streaming
- **Retention**: 3 years (1095 days), 2 versions  
- **Access**: Private with streaming service access
- **Storage**: No compression (preserve quality), no encryption
- **Metadata**: Media specifications, transcoding settings, CDN configuration

**Example files**:
- `video.mp4` - Corporate video with duration/quality metadata
- `audio.wav` - Background audio file

### 🧪 test-import/ → `test-import` S3 bucket
**Use Case**: Development and testing files
- **Retention**: 30 days
- **Security**: Encryption enabled  
- **Metadata**: Environment and project tags

## Settings Files

### Global Settings: `settings.json`
Each bucket directory contains a `settings.json` file defining:

```json
{
  "policies": {
    "retention": { "days": 365, "versions": 5 },
    "access": { "public": false, "allowedOrigins": ["..."] },
    "compression": true,
    "encryption": false
  },
  "metadata": {
    "key": "value",
    "environment": "production"
  }
}
```

### File-Specific Settings: `[filename].settings.json`
Individual files can have specific metadata and policies:

```json
{
  "metadata": {
    "document-type": "contract",
    "status": "executed",
    "value": "50000"
  }
}
```

## How It Works

1. **Bucket Resolution**: Directory name → S3 bucket name (e.g., `documents/` → `documents` bucket)
2. **Policy Application**: Each bucket gets independent retention, access, and storage policies
3. **Metadata Merging**: Global settings + file-specific settings applied to each uploaded object
4. **File Exclusion**: All `.settings.json` files are excluded from upload (configuration only)

## Running the Import

```bash
# Build and run fs-script
make docker-fs-script-build
make dev fs-init up

# Or run locally  
./gradlew :fs-script:fs-script-gateway:bootRun
```

The fs-script will process each directory independently, creating separate S3 buckets with their configured policies and uploading files with appropriate metadata.

## Benefits

- **Separation of Concerns**: Different content types in separate buckets
- **Independent Policies**: Each bucket optimized for its use case
- **Flexible Metadata**: Content-specific tagging and classification
- **Easy Management**: Clear structure for different content types
- **Scalable**: Add new buckets by creating new directories