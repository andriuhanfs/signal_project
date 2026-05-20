# UML Model Explanations

## Alert Generation System

The alert generation model separates signal evaluation, alert representation, and alert routing so that each part has a clear responsibility. `AlertGenerator` receives patient records from storage or the live stream and checks them against patient-specific threshold rules. The threshold objects are associated with a patient identifier, which allows the same vital sign to have different limits for different patients. This matters in a hospital context because a safe heart-rate range or blood-pressure range may depend on the individual case.

`Alert` is modeled as the data object that contains the patient ID, the triggered condition, the timestamp, and the severity or priority information needed by staff. It does not decide whether it should exist; it only represents the result of the evaluation. `AlertManager` is responsible for dispatching generated alerts to medical staff or other notification channels. This keeps routing separate from medical rule checking.

The model also links alert generation to data storage, because alerts need access to recent and historical readings. The association with patient identification ensures that alerts are traceable to the correct patient record without exposing more patient details than necessary. Access is intentionally limited: the generator reads patient data and creates alerts, while the manager dispatches alerts and does not directly modify stored measurements. This supports modularity and makes it easier to add new alert rules later.

## Data Storage System

The data storage model is centered on `DataStorage` as the main repository interface for patient measurements. `PatientData` represents one timestamped vital-sign record, including the patient ID, data type, value, and timestamp. This separation keeps individual measurements simple and makes the storage component responsible for grouping and retrieving them. The system can append new data as it arrives, while still supporting historical queries by patient and time range.

`DataRetriever` is modeled as the controlled access point for medical staff queries. Rather than letting every subsystem inspect storage directly, retrieval requests pass through a class that can apply access rules and validation. Patient measurement values should be treated as numeric `double` values for alert calculations, while labels and record types remain strings. This is important because the CHMS stores sensitive patient information. Staff should only retrieve the records they are authorized to view, while alert generation should only access the measurement data it needs for safety checks.

The diagram includes deletion and retention responsibilities so old records can be removed according to policy. In a real hospital system this would be necessary for privacy, storage limits, and legal compliance. Versioning or timestamp ordering is included so that later analysis can distinguish between current readings and older readings. The relationships are designed to keep storage independent from input sources: file, TCP, and WebSocket listeners can all store records through the same interface without changing retrieval logic.

## Patient Identification System

The patient identification model handles the link between simulator patient IDs and real hospital patient records. `PatientIdentifier` receives an incoming patient ID from a data record and attempts to match it to a `HospitalPatient`. `HospitalPatient` contains identifying details such as internal hospital ID, name, and medical context, but those details are not exposed to the entire monitoring system. This supports privacy by keeping personal information inside the identification subsystem.

`IdentityManager` oversees the matching process and handles edge cases. For example, if an incoming patient ID has no matching hospital record, the manager can mark the data as unmatched, log the issue, or trigger a review. If duplicate or inconsistent mappings are found, the manager is responsible for treating them as anomalies rather than silently attaching data to the wrong patient. In a health monitoring system, a wrong match is more dangerous than a missing match because it can lead to incorrect clinical decisions.

The model keeps identification separate from alert evaluation and storage. Other subsystems should work with verified patient IDs and should not be responsible for deciding identity integrity. Access rules are therefore strict: `PatientIdentifier` can query patient records, `IdentityManager` can resolve or reject mappings, and alert or storage components only receive the confirmed identifier. This makes the design safer, easier to audit, and easier to extend if a real hospital database is added later.

## Data Access Layer

The data access layer model isolates external input protocols from the rest of the CHMS. The core abstraction is `DataListener` or `DataReader`, which defines the behavior expected from any input source. `TCPDataListener`, `WebSocketDataListener`, and `FileDataListener` specialize that behavior for different simulator outputs. This prevents storage and alert generation from needing to know whether data arrived from a live socket, a WebSocket stream, or a file log.

`DataParser` standardizes raw input into patient data records. This is important because different input sources may produce different formats, such as readable log lines or compact CSV-like messages. By centralizing parsing, validation and error handling become consistent. Corrupted messages can be rejected before they enter storage, and valid messages can be converted into the same internal object structure.

`DataSourceAdapter` passes parsed records to the storage layer. It acts as the boundary between external data handling and internal data management. This makes the design easier to extend: adding a new protocol would require a new listener, but not a rewrite of storage or alert logic. Access rules are also clearer. Listeners handle connections, the parser handles format validation, and the adapter sends only clean patient records into `DataStorage`. That separation supports real-time processing while keeping the rest of the system stable.
