package quarano.occasion.web;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.core.web.MappedPayloads;
import quarano.department.TrackedCase;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.occasion.Occasion;
import quarano.occasion.OccasionCode;
import quarano.occasion.OccasionManagement;
import quarano.occasion.web.OccasionRepresentions.OccasionsDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.mvc.MvcLink;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Oliver Drotbohm
 * @author David Bauknecht
 * @since 1.4
 */
@RestController
@RequiredArgsConstructor
class OccasionController {

	private final @NonNull OccasionManagement occasions;
	private final @NonNull OccasionRepresentions representations;

	/**
	 * Returns all occasions available in the system.
	 *
	 * @return will never be {@literal null}.
	 */
	@GetMapping("/hd/occasions")
	HttpEntity<?> getOccasions() {

		var model = HalModelBuilder.emptyHalModel()
				.embed(occasions.findAll().map(this::toModel), LinkRelation.of("occasions"))
				.build();

		return ResponseEntity.ok(model);
	}

	/**
	 * Returns the occasion registered for the given {@link OccasionCode}.
	 *
	 * @param occasionCode must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	@GetMapping("/hd/occasions/{occasionCode:" + OccasionCode.REGEX + "}")
	HttpEntity<?> getOccasion(@PathVariable OccasionCode occasionCode) {

		return occasions.findOccasionBy(occasionCode)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Update the occasion registered for the given {@link OccasionCode}.
	 *
	 * @param occasionCode must not be {@literal null}.
	 * @param payload
	 * @param errors
	 * @return will never be {@literal null}.
	 */
	@PutMapping("/hd/occasions/{occasionCode:" + OccasionCode.REGEX + "}")
	HttpEntity<?> getOccasion(@PathVariable OccasionCode occasionCode, @RequestBody OccasionsDto payload, Errors errors) {
		var existing = occasions.findOccasionBy(occasionCode).orElse(null);
		return MappedPayloads.of(payload, errors)
				.notFoundIf(existing == null)
				.map(it -> occasions.updateOccasionBy(it.title, it.start, it.end, it.street, it.houseNumber, it.zipCode, it.city, it.additionalInformation, it.contactPerson, existing))
				.map(representations::toSummary)
				.concludeIfValid(ResponseEntity::ok);
	}


	/**
	 * Delete the occasion registered for the given {@link OccasionCode}.
	 *
	 * @param occasionCode must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */

	@DeleteMapping("/hd/occasions/{occasionCode:" + OccasionCode.REGEX + "}")
	HttpEntity<?> deleteOccasion(@PathVariable OccasionCode occasionCode) {

		return occasions.findOccasionBy(occasionCode)
				.map(it -> {
					occasions.deleteOccasion(it);
					return ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.build();
				}).orElseGet(() -> ResponseEntity.badRequest().build());
	}

	/**
	 * Creates a new occasion associated with the {@link TrackedCase} identified by the given
	 * {@link TrackedCaseIdentifier}.
	 *
	 * @param trackedCaseId
	 * @param payload
	 * @param errors
	 * @return will never be {@literal null}.
	 */
	@PostMapping("/hd/cases/{id}/occasions")
	HttpEntity<?> postOccasions(@PathVariable("id") TrackedCaseIdentifier trackedCaseId,
			@RequestBody OccasionsDto payload, Errors errors) {

		return MappedPayloads.of(payload, errors)
				.flatMap(it -> occasions.createOccasion(it.title, it.start, it.end, it.street, it.street, it.zipCode, it.city, it.additionalInformation, it.contactPerson,  trackedCaseId))
				.map(representations::toSummary)
				.concludeIfValid(ResponseEntity::ok);
	}

	private EntityModel<OccasionRepresentions.OccasionSummary> toModel(Occasion occasion) {
		return EntityModel.of(representations.toSummary(occasion))
				.add(MvcLink.of(on(OccasionController.class).getOccasion(occasion.getOccasionCode()), IanaLinkRelations.SELF));
	}
}
