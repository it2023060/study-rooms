// @RestController: @Controller + @ResponseBody
// This class will handle HTTP requests and will return data(JASON)
// but wont render HTML pages.
// @RequestMapping("/api/reservations"): Every single method in this
// class will have URLs that start with /api/reservations.
// @Tag: only helps humans understand the API.
// It does NOT change what the API does.
// @SecurityRequirement(name = "bearerAuth"): all endpoints in this controller
// require you to provide a token.
// @GetMapping("/my"): Maps HTTP GET requests to this method
// @PostMapping: Maps HTTP POST requests to this method, used for creating something new.
// @DeleteMapping("/{id}"): Maps HTTP DELETE requests to this method.
// {id}: is the path variable which gets passed to the method.
// @Operation(Summary = "..."): adds a short description of what the endpoint
// does in the API documentation.
// @Valid: validates the incoming req body against rules defined in the DTO
// @RequestBody: Tells Spring to read the HTTP request body and convert
// JSON into a Java object.
// @PathVariable: Reads a value from the URL path.
// Authentication auth: Spring injects the current
// logged in user's authentication info into your method.
//

package gr.hua.dit.studyrooms.controller.api;

import gr.hua.dit.studyrooms.dto.ReservationFormDto;
import gr.hua.dit.studyrooms.entity.Reservation;
import gr.hua.dit.studyrooms.entity.User;
import gr.hua.dit.studyrooms.security.CustomUserDetails;
import gr.hua.dit.studyrooms.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Reservation operations for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class ReservationApiController {

    private final ReservationService reservationService;

    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Gives back all the reservations that belong to the currently logged in user.
    // GET /api/reservations/my
    @Operation(summary = "List reservations for the authenticated user")
    // We call this point with a GET method.
    @GetMapping("/my")
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication auth) {
        // Extract the user(who is logged in)
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        // Extract the actual user entity.
        User user = cud.getUser();

        // Spring converts the returned List<Reservation> into a JSON.
        return ResponseEntity.ok(reservationService.getReservationsForUser(user));
    }

    // POST /api/reservations
    @Operation(summary = "Create a reservation for the authenticated user")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationFormDto form,
                                               Authentication auth) {
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        Reservation r = reservationService.createReservation(
                user,
                form.getStudySpaceId(),
                form.getDate(),
                form.getStartTime(),
                form.getEndTime()
        );
        return ResponseEntity.ok(r);
    }

    // DELETE /api/reservations/{id}
    @Operation(summary = "Cancel one of the authenticated user's reservations")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMyReservation(@PathVariable Long id,
                                                    Authentication auth) {
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        reservationService.cancelReservation(id, user);
        return ResponseEntity.noContent().build();
    }
}
