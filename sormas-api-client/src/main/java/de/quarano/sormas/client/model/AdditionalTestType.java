/*
 * SORMAS REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.44.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package de.quarano.sormas.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets AdditionalTestType
 */
public enum AdditionalTestType {
  HAEMOGLOBINURIA("HAEMOGLOBINURIA"),
  PROTEINURIA("PROTEINURIA"),
  HEMATURIA("HEMATURIA"),
  ARTERIAL_VENOUS_BLOOD_GAS("ARTERIAL_VENOUS_BLOOD_GAS"),
  ALT_SGPT("ALT_SGPT"),
  AST_SGOT("AST_SGOT"),
  CREATININE("CREATININE"),
  POTASSIUM("POTASSIUM"),
  UREA("UREA"),
  HAEMOGLOBIN("HAEMOGLOBIN"),
  TOTAL_BILIRUBIN("TOTAL_BILIRUBIN"),
  CONJ_BILIRUBIN("CONJ_BILIRUBIN"),
  WBC_COUNT("WBC_COUNT"),
  PLATELETS("PLATELETS"),
  PROTHROMBIN_TIME("PROTHROMBIN_TIME");

  private String value;

  AdditionalTestType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AdditionalTestType fromValue(String text) {
    for (AdditionalTestType b : AdditionalTestType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}