// Purpose of StaffApiController, StaffApiController is a staff-only
// REST controller that allows authorized staff members to
// view and manage reservations in the system.
package gr.hua.dit.studyrooms.controller.api;

import gr.hua.dit.studyrooms.entity.Reservation;
import gr.hua.dit.studyrooms.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('STAFF')") // It prevents anyone who is not a staff user from accessing the controller method. Method level security
// If the condition fails the method is never called.
@Tag(name = "Staff", description = "Staff-only reservation management")
@SecurityRequirement(name = "bearerAuth")
public class StaffApiController {

    private final ReservationService reservationService;

    public StaffApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // This endpoint allows staff users to retrieve reservations(from specific dates or all).
    // GET /api/staff/reservations
    @Operation(summary = "List reservations for a specific date or all")
    @GetMapping("/reservations")
    // ResponseEntity<List<Reservation>> : Returns an HTTP response
    // Body contains a list of Reservation objects
    // ResponseEntity lets you control the HTTP status
    public ResponseEntity<List<Reservation>> getReservations(
            @RequestParam(required = false)
            // Tells Spring how to parse the date(e.g. yyyy-MM-dd), converts the query string into a LocalDate
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        // If a date exists, get reservations for this date, if no date, get them all(of all users)
        List<Reservation> reservations;
        if (date != null) {
            reservations = reservationService.getReservationsForDate(date);
        } else {
            reservations = reservationService.getAll();
        }

        return ResponseEntity.ok(reservations);
    }

    // This endpoint allows a STAFF user to cancel any reservation in the system(does not matter who created it)
    // POST /api/staff/reservations/{id}/cancel
    @Operation(summary = "Cancel a reservation as staff")
    @PostMapping("/reservations/{id}/cancel")
    // @PathVariable: Extracts the reservation ID from the URL
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        // Calls the service layer
        // Cancels the reservation with staff privileges
        reservationService.cancelReservationAsStaff(id);
        return ResponseEntity.noContent().build();
    }
}
