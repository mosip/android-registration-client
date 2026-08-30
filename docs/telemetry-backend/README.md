# Telemetry Backend Service

This directory contains the architectural design, data pipeline specifications, and documentation for the real-time telemetry processing backend. The system is engineered to ingest, process, and visualize application performance metrics, user interactions, and crash logs transmitted from the Android Registration Client.

## System Overview

To safeguard client application performance and ensure reliable offline operations, the telemetry pipeline decouples high-throughput data ingestion from downstream analytical processing:

* **Client Buffering & Upload:** Telemetry events are queued locally on the Android device. Once network connectivity is established, logs are transmitted via the **TUS protocol** to handle large or interrupted file transfers safely.
* **High-Throughput Ingestion:** The `tusd-server` receives chunked file uploads and saves completed telemetry files to a shared local storage volume.
* **Zero-Overhead Log Shipping:** A lightweight **Vector** sidecar container monitors the shared volume, parses completed JSON files, and instantly streams them to a distributed message queue.
* **Stream Processing & Validation:** **Apache Spark Streaming** consumes the raw telemetry stream, executing real-time data cleansing, schema validation, and PII filtering.
* **Time-Series Storage:** Cleaned telemetry datasets are persisted in an analytical, time-series data warehouse optimized for rapid aggregations.
* **Real-Time Visualization:** An operational dashboard engine queries the datastore to provide administrators with live monitoring, system health metrics, and crash reporting.

---

## Technical Stack

* **Ingestion Protocol:** [TUS Resumable Upload Protocol](https://tus.io/) for resilient file transfers over HTTPS.
* **File Receiver:** `mosip/tusd-server` (the official TUS protocol implementation).
* **Data Pipeline & Shipper:** `timberio/vector` – a high-performance telemetry agent configured to watch, parse, and route JSON logs.
* **Message Broker:** Apache Kafka (managed via ZooKeeper) for high-throughput, fault-tolerant streaming queues.
* **Stream Processing Engine:** Apache Spark Streaming (for micro-batch processing, validation, and PII masking).
* **Data Warehouse:** Analytical, time-series optimized datastore (e.g., ClickHouse or PostgreSQL).
* **Visualization:** Dashboard engine (e.g., Grafana or Apache Superset) for live operator monitoring.

---

## Architectural Data Flow

The complete end-to-end data pipeline follows a strict reactive and decoupled flow:

```
[ Android Client ] 
       │ (TUS Protocol / HTTPS)
       ▼
[ TUSD Server ] ──────> [ Shared Volume ] ──────> [ Vector Sidecar ]
                                                          │ (TCP / JSON)
                                                          ▼
                                                   [ Apache Kafka ]
                                                          │
                                                          ▼
                                              [ Apache Spark Streaming ]
                                              (Validation & PII Filtering)
                                                          │
                                                          ▼
                                              [ Time-Series Database ]
                                              (ClickHouse / PostgreSQL)
                                                          │
                                                          ▼
                                              [ Operational Dashboard ]
                                              (Grafana / Apache Superset)

```

---

## Pipeline Stage Specifications

### 1. Ingestion & Edge Shipping (`tusd` ➔ `Vector` ➔ `Kafka`)

* **TUSD Server** accepts the chunked data streams from the Android client and writes them out as completed JSON files.
* **Vector** monitors the landing directory, reads new files instantly, flattens the structure, appends an ingestion timestamp (`ingest_timestamp`), and forwards the raw event payload directly to the Kafka topic `registration-client-telemetry`.

### 2. Stream Processing (`Kafka` ➔ `Spark Streaming`)

* **Spark Streaming** connects to the Kafka broker via a micro-batch architecture (e.g., 5-second trigger intervals).
* **Processing Tasks:**
* **Schema Enforcement:** Parses raw string payloads into strongly typed DataFrames matching the telemetry schema.
* **Data Cleansing:** Filters out malformed entries and handles missing parameters.
* **Security & Compliance:** Masks or strips out Personally Identifiable Information (PII) before the data leaves the processing boundary.



### 3. Storage & Analytics (`Spark` ➔ `Database`)

* Validated events are written concurrently into structured tables partitioned by time (e.g., `event_date`) to optimize historical query speeds.
* Indexes are placed on critical high-cardinality fields such as `client_id`, `session_id`, and `log_level`.

### 4. Monitoring (`Database` ➔ `Dashboard`)

* The visual dashboard layer maintains direct read-only access to the database.
* **Core Panels:**
* **System Health:** Active device counts, network sync latencies, and upload completion success rates.
* **Application Metrics:** UI interactions, page load speeds, and feature utilization stats.
* **Stability Matrix:** Real-time crash tracking, unhandled exceptions, and fatal error distribution grouped by client version.