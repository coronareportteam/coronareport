export interface OccasionDto {
  start: Date;
  end: Date;
  title: string;
  address: Address;
  additionalInformation: string;
  contactPerson: string;
  visitorGroups: VisitorGroup[];
  occasionCode: string;
  trackedCaseId: string;
}

interface Address {
  street: string;
  houseNumber: number;
  city: string;
  zipCode: number;
}

interface VisitorGroup {
  visitors: [];
  start: Date;
  end: Date;
  occasionCode: string;
  comment: string;
  locationName: string;
}
