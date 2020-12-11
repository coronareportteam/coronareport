package quarano.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines which roles exist in the application
 *
 * @author Patrick Otto
 * @author Oliver Drotbohm
 */
@AllArgsConstructor
public enum RoleType {

	ROLE_USER("ROLE_USER", false), //
	ROLE_HD_ADMIN("ROLE_HD_ADMIN", true), //
	ROLE_HD_CASE_AGENT("ROLE_HD_CASE_AGENT", true), //
	ROLE_QUARANO_ADMIN("ROLE_QUARANO_ADMIN", false), //
	ROLE_THIRD_PARTY("ROLE_THIRD_PARTY", false);

	@Getter private String code;
	@Getter private boolean isDepartmentRole;
}
