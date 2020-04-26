package quarano.reference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import quarano.core.web.ErrorsDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Transactional
@RestController
@RequiredArgsConstructor
class SymptomController {

	private static final Sort BY_NAME_ASCENDING = Sort.sort(Symptom.class).by(Symptom::getName).ascending();

	private final @NonNull SymptomRepository symptoms;
	private final @NonNull ModelMapper modelMapper;
	private final @NonNull MessageSourceAccessor messages;

	/**
	 * Returns all symptom entries. Should be used as master-data for other api calls;
	 *
	 * @return
	 */
	@GetMapping("/api/symptoms")
	public Stream<SymptomDto> getSymptoms() {

		return symptoms.findAll(BY_NAME_ASCENDING) //
				.map(it -> modelMapper.map(it, SymptomDto.class)) //
				.stream();
	}

	/**
	 * Stores a new Symptom
	 *
	 * @param symptomDto
	 * @return
	 */
	@PostMapping("/api/symptoms")
	public HttpEntity<?> addSymptom(@Valid @RequestBody SymptomDto symptomDto, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(ErrorsDto.of(errors, messages));
		}

		var symptom = modelMapper.map(symptomDto, Symptom.class);
		var savedSymptom = modelMapper.map(symptoms.save(symptom), SymptomDto.class);

		return ResponseEntity.ok().body(savedSymptom);
	}

	/**
	 * //TODO: needed?
	 * Stores an array
	 *
	 * @param symptomDtos
	 * @return
	 */
	@PostMapping("/api/allsymptoms")
	public Stream<SymptomDto> addSymptoms(@Valid @RequestBody List<SymptomDto> symptomDtos) {

		symptomDtos.stream() //
				.map(x -> modelMapper.map(x, Symptom.class)) //
				.forEach(symptoms::save);

		return symptoms.findAll() //
				.map(x -> modelMapper.map(x, SymptomDto.class)) //
				.stream();
	}
}
