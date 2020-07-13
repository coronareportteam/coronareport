# CaseDataDto

## Properties

| Name                                      | Type                                                        | Description | Notes      |
| ----------------------------------------- | ----------------------------------------------------------- | ----------- | ---------- |
| **creationDate**                          | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **changeDate**                            | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **uuid**                                  | **String**                                                  |             | [optional] |
| **pseudonymized**                         | **Boolean**                                                 |             | [optional] |
| **disease**                               | [**Disease**](Disease.md)                                   |             |
| **diseaseDetails**                        | **String**                                                  |             | [optional] |
| **plagueType**                            | [**PlagueType**](PlagueType.md)                             |             | [optional] |
| **dengueFeverType**                       | [**DengueFeverType**](DengueFeverType.md)                   |             | [optional] |
| **rabiesType**                            | [**RabiesType**](RabiesType.md)                             |             | [optional] |
| **person**                                | [**PersonReferenceDto**](PersonReferenceDto.md)             |             |
| **epidNumber**                            | **String**                                                  |             | [optional] |
| **reportDate**                            | [**OffsetDateTime**](OffsetDateTime.md)                     |             |
| **reportingUser**                         | [**UserReferenceDto**](UserReferenceDto.md)                 |             |
| **regionLevelDate**                       | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **nationalLevelDate**                     | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **districtLevelDate**                     | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **caseClassification**                    | [**CaseClassification**](CaseClassification.md)             |             |
| **classificationUser**                    | [**UserReferenceDto**](UserReferenceDto.md)                 |             | [optional] |
| **classificationDate**                    | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **classificationComment**                 | **String**                                                  |             | [optional] |
| **investigationStatus**                   | [**InvestigationStatus**](InvestigationStatus.md)           |             |
| **investigatedDate**                      | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **outcome**                               | [**CaseOutcome**](CaseOutcome.md)                           |             | [optional] |
| **outcomeDate**                           | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **sequelae**                              | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **sequelaeDetails**                       | **String**                                                  |             | [optional] |
| **region**                                | [**RegionReferenceDto**](RegionReferenceDto.md)             |             |
| **district**                              | [**DistrictReferenceDto**](DistrictReferenceDto.md)         |             |
| **community**                             | [**CommunityReferenceDto**](CommunityReferenceDto.md)       |             | [optional] |
| **healthFacility**                        | [**FacilityReferenceDto**](FacilityReferenceDto.md)         |             |
| **healthFacilityDetails**                 | **String**                                                  |             | [optional] |
| **pregnant**                              | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **vaccination**                           | [**Vaccination**](Vaccination.md)                           |             | [optional] |
| **vaccinationDoses**                      | **String**                                                  |             | [optional] |
| **vaccinationDate**                       | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **vaccinationInfoSource**                 | [**VaccinationInfoSource**](VaccinationInfoSource.md)       |             | [optional] |
| **vaccine**                               | **String**                                                  |             | [optional] |
| **smallpoxVaccinationScar**               | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **smallpoxVaccinationReceived**           | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **surveillanceOfficer**                   | [**UserReferenceDto**](UserReferenceDto.md)                 |             | [optional] |
| **clinicianName**                         | **String**                                                  |             | [optional] |
| **clinicianPhone**                        | **String**                                                  |             | [optional] |
| **clinicianEmail**                        | **String**                                                  |             | [optional] |
| **notifyingClinic**                       | [**HospitalWardType**](HospitalWardType.md)                 |             | [optional] |
| **notifyingClinicDetails**                | **String**                                                  |             | [optional] |
| **caseOfficer**                           | [**UserReferenceDto**](UserReferenceDto.md)                 |             | [optional] |
| **reportLat**                             | **Double**                                                  |             | [optional] |
| **reportLon**                             | **Double**                                                  |             | [optional] |
| **reportLatLonAccuracy**                  | **Float**                                                   |             | [optional] |
| **hospitalization**                       | [**HospitalizationDto**](HospitalizationDto.md)             |             | [optional] |
| **symptoms**                              | [**SymptomsDto**](SymptomsDto.md)                           |             | [optional] |
| **epiData**                               | [**EpiDataDto**](EpiDataDto.md)                             |             | [optional] |
| **therapy**                               | [**TherapyDto**](TherapyDto.md)                             |             | [optional] |
| **clinicalCourse**                        | [**ClinicalCourseDto**](ClinicalCourseDto.md)               |             | [optional] |
| **maternalHistory**                       | [**MaternalHistoryDto**](MaternalHistoryDto.md)             |             | [optional] |
| **creationVersion**                       | **String**                                                  |             | [optional] |
| **portHealthInfo**                        | [**PortHealthInfoDto**](PortHealthInfoDto.md)               |             | [optional] |
| **caseOrigin**                            | [**CaseOrigin**](CaseOrigin.md)                             |             | [optional] |
| **pointOfEntry**                          | [**PointOfEntryReferenceDto**](PointOfEntryReferenceDto.md) |             | [optional] |
| **pointOfEntryDetails**                   | **String**                                                  |             | [optional] |
| **additionalDetails**                     | **String**                                                  |             | [optional] |
| **externalID**                            | **String**                                                  |             | [optional] |
| **sharedToCountry**                       | **Boolean**                                                 |             | [optional] |
| **quarantine**                            | [**QuarantineType**](QuarantineType.md)                     |             | [optional] |
| **quarantineTypeDetails**                 | **String**                                                  |             | [optional] |
| **quarantineFrom**                        | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **quarantineTo**                          | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **quarantineHelpNeeded**                  | **String**                                                  |             | [optional] |
| **quarantineOrderedVerbally**             | **Boolean**                                                 |             | [optional] |
| **quarantineOrderedOfficialDocument**     | **Boolean**                                                 |             | [optional] |
| **quarantineOrderedVerballyDate**         | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **quarantineOrderedOfficialDocumentDate** | [**OffsetDateTime**](OffsetDateTime.md)                     |             | [optional] |
| **quarantineHomePossible**                | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **quarantineHomePossibleComment**         | **String**                                                  |             | [optional] |
| **quarantineHomeSupplyEnsured**           | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **quarantineHomeSupplyEnsuredComment**    | **String**                                                  |             | [optional] |
| **reportingType**                         | [**ReportingType**](ReportingType.md)                       |             | [optional] |
| **postpartum**                            | [**YesNoUnknown**](YesNoUnknown.md)                         |             | [optional] |
| **trimester**                             | [**Trimester**](Trimester.md)                               |             | [optional] |
| **unreferredPortHealthCase**              | **Boolean**                                                 |             | [optional] |