package quarano.occasion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.core.Address;
import quarano.core.ZipCode;
import quarano.core.DataInitializer;
import quarano.department.TrackedCaseDataInitializer;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

/**
 * @author David Bauknecht
 * @author Oliver Drotbohm
 * @since 1.4
 */
@Component
@RequiredArgsConstructor
public class OccasionDataInitializer implements DataInitializer {

	public final static OccasionCode OCCASION_CODE_1 = OccasionCode.of("EVENTNUM");

	private final @NonNull OccasionRepository occasions;

	/*
	 * (non-Javadoc)
	 * @see quarano.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		occasions.save(new Occasion("Sample event", LocalDate.now().atStartOfDay(),
				LocalDate.now().plusDays(1).atStartOfDay(), new Address("Musterstraße", Address.HouseNumber.of("2"), "Musterstadt", ZipCode.of("12345")),"","", OCCASION_CODE_1, TrackedCaseDataInitializer.TRACKED_CASE_MARKUS));
	}
}
