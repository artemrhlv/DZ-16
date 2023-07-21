package tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookingDateBody {

    private String checkin;
    private String checkout;

}
