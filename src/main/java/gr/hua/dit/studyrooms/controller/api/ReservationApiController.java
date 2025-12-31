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

@RestController // This class will handle HTTP requests and will return data(JASON) but wont render HTML pages.
@RequestMapping("/api/reservations") // @RequestMapping("/api/reservations"): Every single method in this class will have URLs that start with /api/reservations.
@Tag(name = "Reservations", description = "Reservation operations for authenticated users")
@SecurityRequirement(name = "bearerAuth") // @SecurityRequirement(name = "bearerAuth"): all endpoints in this controller require you to provide a token.
public class ReservationApiController {

    private final ReservationService reservationService;

    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Gives back all the reservations that belong to the currently logged in user.
    // GET /api/reservations/my
    @Operation(summary = "List reservations for the authenticated user")
    // We call this point with a GET method.
    @GetMapping("/my") // @GetMapping("/my"): Maps HTTP GET requests to this method
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
    @PostMapping // If someone sends a POST request to this URL, run this specific block, used for creating something new.
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationFormDto form,
                                               Authentication auth) {
        // The information is sent in a DTO form
        // @Valid: cheks teh info make sense
        // @RequestBody: Grabs the data out of the incoming
        // messages and puts it into a Java object called form.

        // Finds out who is logged in
        // Take the security login auth, look inside, and find the
        // identity cud, finally pull ou the actual User object so the systems knows,
        // whos's name to put on reservation.
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        // This part calls the service, this service cheks if the room is free and saves it on the database.
        Reservation r = reservationService.createReservation(
                user,
                form.getStudySpaceId(),
                form.getDate(),
                form.getStartTime(),
                form.getEndTime()
        );
        // this sends a message back to the user saying everything went ok
        return ResponseEntity.ok(r);
    }

    // DELETE /api/reservations/{id}
    // Adds documentation for API tools like Swagger UI
    @Operation(summary = "Cancel one of the authenticated user's reservations")
    // This notation says what this method will handle HTTP DELETE Requests.
    // URL must much /api/reservations/{id}.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMyReservation(@PathVariable Long id,
                                                    Authentication auth) {
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        // Cancel the reservation with this ID, but only if it belongs to this user.
        reservationService.cancelReservation(id, user);
        // The request succeeded, and there’s nothing to return.
        return ResponseEntity.noContent().build();
    }
}
