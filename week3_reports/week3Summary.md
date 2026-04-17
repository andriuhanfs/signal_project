# Week 3 Test and Coverage Summary

## Unit Test Verification

The Unit tests were executed successfully with Maven.

Command used:

```sh
mvn clean test
```

Result:

```text
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The test suite covers patient record filtering, data storage behavior, file-based data reading, and alert generation rules.

## Code Coverage Report

JaCoCo plugin was used to generate the code coverage report.

Command used:

```sh
mvn test jacoco:report
```

Generated report location:

```text
target/site/jacoco/index.html
```

The `target/` directory is generated build output, so the HTML report can be regenerated locally with the command above.

## Tested Areas

- `Patient.getRecords(...)`
- `DataStorage.addPatientData(...)`
- `DataStorage.getRecords(...)`
- `DataStorage.getAllPatients(...)`
- `FileDataReader`

- percentage values such as `95%`
- alert text values such as `triggered` and `resolved`
- malformed file lines and invalid numeric values
- blood pressure threshold alerts
- blood pressure trend alerts
- low oxygen saturation alerts
- rapid oxygen saturation drop alerts
- hypotensive hypoxemia alerts
- abnormal ECG peak alerts
- manual alert button records

## Not Fully Tested

The TCP and WebSocket output classes weren't tested because they need network connections. I also did not test the real-time scheduling in `HealthDataSimulator`, because that depends on timed background execution.


