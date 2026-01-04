// It lets staff users ask: "How occupied was this study space each day between these two dates"
// The API layer is the entry point of the application; it receives HTTP requests,
// coordinates between DTOs and the business logic layer, and returns appropriate
// responses.
// Simpler: the controller layer connects the outside world to the business logic.
package gr.hua.dit.studyrooms.controller.api;

import gr.hua.dit.studyrooms.dto.OccupancyStatsEntry;
import gr.hua.dit.studyrooms.entity.StudySpace;
import gr.hua.dit.studyrooms.service.ReservationStatisticsService;
import gr.hua.dit.studyrooms.service.StudySpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Statistics", description = "Staff-only statistics endpoints")
public class StatsApiController {

    // Uses two services: StudySpaceService and ReservationStatisticsService
    private final ReservationStatisticsService reservationStatisticsService;
    private final StudySpaceService studySpaceService;

    public StatsApiController(ReservationStatisticsService reservationStatisticsService,
                              StudySpaceService studySpaceService) {
        this.reservationStatisticsService = reservationStatisticsService;
        this.studySpaceService = studySpaceService;
    }

    @Operation(summary = "Daily occupancy for a space", description = "Returns occupancy metrics between the given dates.")
    // Only users with the STAFF role can access the endpoint.\
    // If the user is not authorized, spring returns 403 Forbidden(the method body isn't executed)
    @PreAuthorize("hasRole('STAFF')")
    // FUll endpoint URL: GET /api/stats/occupancy
    @GetMapping("/occupancy")
    public ResponseEntity<List<OccupancyStatsEntry>> occupancy(
            @RequestParam("spaceId") Long spaceId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        StudySpace space = studySpaceService.getSpaceById(spaceId);
        List<OccupancyStatsEntry> stats = reservationStatisticsService.getDailyOccupancy(space, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}
