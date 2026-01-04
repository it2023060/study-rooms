// This is a Spring Boot REST controller that handles HTTP requests related to study spaces
// It provides API endpoints to manage study spaces
// It is an interface between the client and the service layer
package gr.hua.dit.studyrooms.controller.api;

import gr.hua.dit.studyrooms.dto.StudySpaceDto;
import gr.hua.dit.studyrooms.entity.StudySpace;
import gr.hua.dit.studyrooms.service.StudySpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// All endpoints start with /api/spaces
@RequestMapping("/api/spaces")
@Tag(name = "Spaces", description = "Study space management")
@SecurityRequirement(name = "bearerAuth")
public class StudySpaceApiController {

    // contains the business logic for study spaces
    private final StudySpaceService studySpaceService;

    public StudySpaceApiController(StudySpaceService studySpaceService) {
        this.studySpaceService = studySpaceService;
    }

    // GET /api/spaces
    @Operation(summary = "List all study spaces")
    @GetMapping // When client sends a GET(/api/spaces) request this method runs
    // This method returns an HTTP response that contains a list of study spaces
    public ResponseEntity<List<StudySpace>> getAllSpaces() {
        return ResponseEntity.ok(studySpaceService.getAllSpaces());
    }

    // This method returns a specified(id) study space
    // GET /api/spaces/{id}
    @Operation(summary = "Get a study space by id")
    @GetMapping("/{id}")
    // @PathVariable: tells spring to take the {id} form the URL and pass it as a method parameter.
    public ResponseEntity<StudySpace> getSpace(@PathVariable Long id) {
        // Returns a single Study Space object
        // Calls the service layer
        return ResponseEntity.ok(studySpaceService.getSpaceById(id));
        // ResponseEntity.ok(): Wraps the returned study space in a 200 OK HTTP response.
    }

    // POST /api/spaces (STAFF)
    @Operation(summary = "Create a new study space (staff only)")
    // POST is the standard method in REST for creating a new resource.
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF')")
    // @RequestBody StudySpaceDto space: Spring automatically maps JSON in the HTTP request body to a studySpaceDto object
    public ResponseEntity<StudySpace> createSpace(@Valid @RequestBody StudySpaceDto space) {
        // studySpaceService.createSpace(fromDto(space)): create StudySpace object
        return ResponseEntity.ok(studySpaceService.createSpace(fromDto(space)));
    }

    // allows STAFF users to update an existing study space by its ID.
    // PUT /api/spaces/{id} (STAFF)
    @Operation(summary = "Update a study space (staff only)")
    // Maps this method to HTTP PUT requests.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF')")
    public ResponseEntity<StudySpace> updateSpace(@PathVariable Long id,
                                                  @Valid @RequestBody StudySpaceDto space) {
        // studySpaceService.updateSpace(id, ...): calls the service layer to find existing study space by id
        // update its fields with the new data, save the changes to the database.
        return ResponseEntity.ok(studySpaceService.updateSpace(id, fromDto(space)));
        // fromDto(space): converts the incoming StudySpaceDto into a StudySpace entity.
    }

    // Allows STAFF users to delete one{id} study space
    // DELETE /api/spaces/{id} (STAFF)
    @Operation(summary = "Delete a study space (staff only)")
    // Maps this method to HTTP DELETE requests.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF')")
    // @PathVariable Long id: extracts the {id} value from the URL
    // Identifies which study dpace should be deleted
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        // Calls the service layer to perform the deletion.
        studySpaceService.deleteSpace(id);
        return ResponseEntity.noContent().build();
    }

    // private, this method is only used inside this controller.
    // It returns a study space entity
    private StudySpace fromDto(StudySpaceDto dto) {
        StudySpace space = new StudySpace();
        space.setId(dto.getId());
        space.setName(dto.getName());
        space.setDescription(dto.getDescription());
        space.setCapacity(dto.getCapacity());
        space.setOpenTime(dto.getOpenTime());
        space.setCloseTime(dto.getCloseTime());
        return space;
    }
}
