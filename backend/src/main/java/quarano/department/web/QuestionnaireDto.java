package quarano.department.web;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.validation.Errors;

import lombok.Data;
import lombok.Setter;
import quarano.core.validation.Textual;
import quarano.department.Questionnaire;
import quarano.reference.Language;
import quarano.reference.Symptom;
import quarano.reference.SymptomRepository;

@Data
public class QuestionnaireDto {

	private @NotNull @Setter Boolean hasSymptoms;
	private @PastOrPresent LocalDate dayOfFirstSymptoms;
	private List<UUID> symptoms;

	private @Textual String familyDoctor;
	private @Textual String guessedOriginOfInfection;

	private @NotNull @Setter Boolean hasPreExistingConditions;
	private @Textual String hasPreExistingConditionsDescription;

	private @NotNull @Setter Boolean belongToMedicalStaff;
	private @Textual String belongToMedicalStaffDescription;

	private @Setter Boolean hasContactToVulnerablePeople;
	private @Textual String hasContactToVulnerablePeopleDescription;

	Questionnaire applyTo(Questionnaire report, SymptomRepository symptoms, Language lang) {

		List<Symptom> symptomsOfReport = Collections.emptyList();

		if (dayOfFirstSymptoms != null) {

			if (null != this.symptoms) {
				symptomsOfReport = this.symptoms.stream()
						.map(it -> symptoms.findById(it).get().translate(lang))
						.collect(Collectors.toList());
			}

			report.withFirstSymptomsAt(dayOfFirstSymptoms, symptomsOfReport);
		}

		if (hasPreExistingConditionsDescription != null) {
			report.withPreExistingConditions(hasPreExistingConditionsDescription);
		}

		if (belongToMedicalStaffDescription != null) {
			report.withBelongToMedicalStaff(belongToMedicalStaffDescription);
		}

		if (hasContactToVulnerablePeopleDescription != null) {
			report.withContactToVulnerablePeople(hasContactToVulnerablePeopleDescription);
		}

		if (hasSymptoms != null) {
			report = hasSymptoms
					? report.withFirstSymptomsAt(dayOfFirstSymptoms, symptomsOfReport)
					: report.withoutSymptoms();
		}

		if (hasPreExistingConditions != null) {
			report = hasPreExistingConditions
					? report.withPreExistingConditions(hasPreExistingConditionsDescription)
					: report.withoutPreExistingConditions();
		}

		if (belongToMedicalStaff != null) {
			report = belongToMedicalStaff
					? report.withBelongToMedicalStaff(belongToMedicalStaffDescription)
					: report.withoutBelongToMedicalStaff();
		}

		if (hasContactToVulnerablePeople != null) {
			report = hasContactToVulnerablePeople
					? report.withContactToVulnerablePeople(hasContactToVulnerablePeopleDescription)
					: report.withoutContactToVulnerablePeople();
		}

		return report;
	}

	QuestionnaireDto validate(Errors errors) {

		if (hasNoDescription(hasPreExistingConditions, hasPreExistingConditionsDescription)) {
			errors.rejectValue("hasPreExistingConditionsDescription",
					"NotNull.IntialReportDto.hasPreExistingConditionsDescription");
		}

		if (hasNoDescription(belongToMedicalStaff, belongToMedicalStaffDescription)) {
			errors.rejectValue("belongToMedicalStaffDescription", "NotNull.IntialReportDto.belongToMedicalStaffDescription");
		}

		if (hasNoDescription(hasContactToVulnerablePeople,hasContactToVulnerablePeopleDescription)) {
			errors.rejectValue("hasContactToVulnerablePeopleDescription",
					"NotNull.IntialReportDto.hasContactToVulnerablePeopleDescription");
		}

		if (Boolean.TRUE.equals(hasSymptoms) && dayOfFirstSymptoms == null) {
			errors.rejectValue("dayOfFirstSymptoms", "NotNull.IntialReportDto.dayOfFirstSymptoms");
		}

		return this;
	}

	boolean hasNoDescription(Boolean indicator, String description) {
		return (Boolean.TRUE.equals(indicator) && (description == null || description.isBlank()));
	}
}
