package quarano.account.web;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.account.Account;
import quarano.account.Account.AccountIdentifier;
import quarano.account.AccountService;
import quarano.account.Department;
import quarano.account.Password.UnencryptedPassword;
import quarano.account.RoleType;
import quarano.account.web.StaffAccountRepresentations.StaffAccountCreateInputDto;
import quarano.account.web.StaffAccountRepresentations.StaffAccountUpdateInputDto;
import quarano.core.EmailAddress;
import quarano.core.web.ErrorsDto;
import quarano.core.web.LoggedIn;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class StaffAccountController {

	private final @NonNull AccountService accounts;
	private final @NonNull StaffAccountRepresentations representations;
	private final @NonNull MessageSourceAccessor accessor;
	

	@PostMapping("/api/hd/accounts")
	public HttpEntity<?> addStaffAccount(@Validated @RequestBody StaffAccountCreateInputDto payload,
										 Errors errors,
										 @LoggedIn Department department) {

		// start validation manually because of required reference to account object from database
		payload.validate(errors, accounts);
		
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(ErrorsDto.of(errors, accessor));
		}
		
		var storedAccount = accounts.createStaffAccount(payload.getUsername(),  //
				UnencryptedPassword.of(payload.getPassword()), //
				payload.getFirstName(), // 
				payload.getLastName(),  //
				EmailAddress.of(payload.getEmail()), //
				department.getId(), //
				payload.getRoles().stream() //
					.map(it -> RoleType.valueOf(it)) //
					.collect(Collectors.toList()));		
		
		var location = on(StaffAccountController.class).getStaffAccount(storedAccount.getId(), storedAccount);

		return ResponseEntity //
				.created(URI.create(fromMethodCall(location).toUriString())) //
				.body(representations.toSummary(storedAccount));		
	}
	

	@PutMapping("/api/hd/accounts/{accountId}")
	public HttpEntity<?> updateStaffAccount(@PathVariable AccountIdentifier accountId,
											@Validated @RequestBody StaffAccountUpdateInputDto payload, Errors errors,
											@LoggedIn Department department) {

		var existing = accounts.findById(accountId).orElse(null);

		if (existing == null) {
			return ResponseEntity.notFound().build();
		}
		
		// start validation manually because of required reference to account object from database
		payload.validate(errors, existing, accounts);
		
		var errorsDto = ErrorsDto.of(errors, accessor);

		return errorsDto.toBadRequestOrElse(() -> ResponseEntity.of(accounts.findById(accountId) //
				.map(it -> representations.from(it, payload)) //
				.map(accounts::saveStaffAccount) //
				.map(representations::toSummary)));
	}		

	@GetMapping(path = "/api/hd/accounts", produces = MediaTypes.HAL_JSON_VALUE)
	public RepresentationModel<?> getStaffAccounts(@LoggedIn Account admin) {

		var departmentAccounts = accounts.findStaffAccountsFor(admin.getDepartmentId());

		return departmentAccounts.stream() //
				.map(it -> representations.toSummary(it)) //
				.collect(Collectors.collectingAndThen(Collectors.toUnmodifiableList(), CollectionModel::of));
	}

	@GetMapping(path = "/api/hd/accounts/{accountId}" , produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<?> getStaffAccount(@PathVariable AccountIdentifier accountId, @LoggedIn Account admin) {

		var adminDepartmentId = admin.getDepartmentId();

		return ResponseEntity.of(accounts.findById(accountId) //
				.filter(it -> it.belongsTo(adminDepartmentId)) //
				.map(it -> representations.toSummary(it)));
	}

	@DeleteMapping("/api/hd/accounts/{accountId}")
	public HttpEntity<?> deleteStaffAccounts(@PathVariable AccountIdentifier accountId, @LoggedIn Account admin) {

		Optional<Account> account = accounts.findById(accountId);
		
		if(account.isPresent())
		{
			accounts.deleteAccount(accountId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
		}
		else {
			return ResponseEntity.badRequest().build();
		}

	}
	
}
