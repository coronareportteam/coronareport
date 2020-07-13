# ContactDto

## Properties

| Name                                            | Type                                                  | Description | Notes      |
| ----------------------------------------------- | ----------------------------------------------------- | ----------- | ---------- |
| **creationDate**                                | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **changeDate**                                  | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **uuid**                                        | **String**                                            |             | [optional] |
| **caze**                                        | [**CaseReferenceDto**](CaseReferenceDto.md)           |             | [optional] |
| **caseIdExternalSystem**                        | **String**                                            |             | [optional] |
| **caseOrEventInformation**                      | **String**                                            |             | [optional] |
| **disease**                                     | [**Disease**](Disease.md)                             |             | [optional] |
| **diseaseDetails**                              | **String**                                            |             | [optional] |
| **reportDateTime**                              | [**OffsetDateTime**](OffsetDateTime.md)               |             |
| **reportingUser**                               | [**UserReferenceDto**](UserReferenceDto.md)           |             |
| **reportLat**                                   | **Double**                                            |             | [optional] |
| **reportLon**                                   | **Double**                                            |             | [optional] |
| **reportLatLonAccuracy**                        | **Float**                                             |             | [optional] |
| **region**                                      | [**RegionReferenceDto**](RegionReferenceDto.md)       |             | [optional] |
| **district**                                    | [**DistrictReferenceDto**](DistrictReferenceDto.md)   |             | [optional] |
| **lastContactDate**                             | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **contactProximity**                            | [**ContactProximity**](ContactProximity.md)           |             | [optional] |
| **contactProximityDetails**                     | **String**                                            |             | [optional] |
| **contactCategory**                             | [**ContactCategory**](ContactCategory.md)             |             | [optional] |
| **contactClassification**                       | [**ContactClassification**](ContactClassification.md) |             | [optional] |
| **contactStatus**                               | [**ContactStatus**](ContactStatus.md)                 |             | [optional] |
| **followUpStatus**                              | [**FollowUpStatus**](FollowUpStatus.md)               |             | [optional] |
| **followUpComment**                             | **String**                                            |             | [optional] |
| **followUpUntil**                               | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **overwriteFollowUpUntil**                      | **Boolean**                                           |             | [optional] |
| **description**                                 | **String**                                            |             | [optional] |
| **relationToCase**                              | [**ContactRelation**](ContactRelation.md)             |             | [optional] |
| **relationDescription**                         | **String**                                            |             | [optional] |
| **externalID**                                  | **String**                                            |             | [optional] |
| **highPriority**                                | **Boolean**                                           |             | [optional] |
| **immunosuppressiveTherapyBasicDisease**        | [**YesNoUnknown**](YesNoUnknown.md)                   |             | [optional] |
| **immunosuppressiveTherapyBasicDiseaseDetails** | **String**                                            |             | [optional] |
| **careForPeopleOver60**                         | [**YesNoUnknown**](YesNoUnknown.md)                   |             | [optional] |
| **quarantine**                                  | [**QuarantineType**](QuarantineType.md)               |             | [optional] |
| **quarantineTypeDetails**                       | **String**                                            |             | [optional] |
| **quarantineFrom**                              | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **quarantineTo**                                | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **person**                                      | [**PersonReferenceDto**](PersonReferenceDto.md)       |             |
| **contactOfficer**                              | [**UserReferenceDto**](UserReferenceDto.md)           |             | [optional] |
| **resultingCase**                               | [**CaseReferenceDto**](CaseReferenceDto.md)           |             | [optional] |
| **resultingCaseUser**                           | [**UserReferenceDto**](UserReferenceDto.md)           |             | [optional] |
| **quarantineHelpNeeded**                        | **String**                                            |             | [optional] |
| **quarantineOrderedVerbally**                   | **Boolean**                                           |             | [optional] |
| **quarantineOrderedOfficialDocument**           | **Boolean**                                           |             | [optional] |
| **quarantineOrderedVerballyDate**               | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **quarantineOrderedOfficialDocumentDate**       | [**OffsetDateTime**](OffsetDateTime.md)               |             | [optional] |
| **quarantineHomePossible**                      | [**YesNoUnknown**](YesNoUnknown.md)                   |             | [optional] |
| **quarantineHomePossibleComment**               | **String**                                            |             | [optional] |
| **quarantineHomeSupplyEnsured**                 | [**YesNoUnknown**](YesNoUnknown.md)                   |             | [optional] |
| **quarantineHomeSupplyEnsuredComment**          | **String**                                            |             | [optional] |
| **additionalDetails**                           | **String**                                            |             | [optional] |