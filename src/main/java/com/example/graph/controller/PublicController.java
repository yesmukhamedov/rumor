package com.example.graph.controller;

import com.example.graph.converter.GraphSnapshot;
import com.example.graph.converter.JsonLdConverter;
import com.example.graph.converter.JsonLdDocument;
import com.example.graph.service.PublicGraphService;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.PublicGraphPostRequest;
import com.example.graph.web.PublicValuesPatchRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/public", produces = "application/ld+json")
@Tag(name = "Public Graph API")
public class PublicController {
    private final PublicGraphService publicGraphService;
    private final JsonLdConverter jsonLdConverter;
    private static final Pattern LOCAL_DATETIME_PATTERN = Pattern.compile(
        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2}(\\.\\d{1,9})?)?$");

    public PublicController(PublicGraphService publicGraphService,
                            JsonLdConverter jsonLdConverter) {
        this.publicGraphService = publicGraphService;
        this.jsonLdConverter = jsonLdConverter;
    }

    @GetMapping(value = "/graph", produces = "application/ld+json")
    @Operation(summary = "Get the public graph snapshot")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JSON-LD graph snapshot",
            content = @Content(mediaType = "application/ld+json",
                schema = @Schema(implementation = JsonLdDocument.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = com.example.graph.web.problem.ProblemDetails.class)))
    })
    public ResponseEntity<JsonLdDocument> getGraph(
        @Parameter(description = "Optional node id to scope to a 1-hop neighborhood")
        @RequestParam(name = "nodeId", required = false) String nodeIdParam,
        @Parameter(description = "Optional ISO-8601 datetime with offset or Z")
        @RequestParam(name = "at", required = false) String atParam) {
        Long nodeId = parseLong(nodeIdParam, "nodeId");
        TimeSlice timeSlice = resolveTimeSlice(atParam);
        GraphSnapshot snapshot = publicGraphService.loadGraph(nodeId, timeSlice.requested(), timeSlice.resolved());
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf("application/ld+json"))
            .body(jsonLdConverter.toJsonLd(snapshot));
    }

    @PostMapping(path = "/graph", produces = "application/ld+json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Apply a public graph update and return the new snapshot")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JSON-LD graph snapshot",
            content = @Content(mediaType = "application/ld+json",
                schema = @Schema(implementation = JsonLdDocument.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = com.example.graph.web.problem.ProblemDetails.class))),
        @ApiResponse(responseCode = "409", description = "Conflict",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = com.example.graph.web.problem.ProblemDetails.class)))
    })
    public ResponseEntity<JsonLdDocument> postGraph(@RequestBody PublicGraphPostRequest request) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        publicGraphService.applyGraph(request, now);
        GraphSnapshot snapshot = publicGraphService.loadGraph(null, null, now);
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf("application/ld+json"))
            .body(jsonLdConverter.toJsonLd(snapshot));
    }

    @PatchMapping(path = "/values", produces = "application/ld+json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Patch a node or edge value and return the new snapshot")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JSON-LD graph snapshot",
            content = @Content(mediaType = "application/ld+json",
                schema = @Schema(implementation = JsonLdDocument.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = com.example.graph.web.problem.ProblemDetails.class))),
        @ApiResponse(responseCode = "409", description = "Conflict",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = com.example.graph.web.problem.ProblemDetails.class)))
    })
    public ResponseEntity<JsonLdDocument> patchValues(@RequestBody PublicValuesPatchRequest request) {
        publicGraphService.applyValuesPatch(request);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        GraphSnapshot snapshot = publicGraphService.loadGraph(null, null, now);
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf("application/ld+json"))
            .body(jsonLdConverter.toJsonLd(snapshot));
    }

    private Long parseLong(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException(field + " must be a number.",
                List.of(new com.example.graph.web.problem.ProblemFieldError(field, field + " must be a number.")));
        }
    }

    private TimeSlice resolveTimeSlice(String requestedAt) {
        if (requestedAt == null || requestedAt.isBlank()) {
            return new TimeSlice(null, OffsetDateTime.now(ZoneOffset.UTC));
        }
        String raw = requestedAt;
        String trimmed = requestedAt.trim();
        try {
            OffsetDateTime parsed = OffsetDateTime.parse(trimmed);
            return new TimeSlice(raw, parsed.withOffsetSameInstant(ZoneOffset.UTC));
        } catch (DateTimeParseException ex) {
            if (LOCAL_DATETIME_PATTERN.matcher(trimmed).matches()) {
                throw new ValidationException("Datetime must include offset.",
                    List.of(new com.example.graph.web.problem.ProblemFieldError("at",
                        "Datetime must include offset.")));
            }
            throw new ValidationException("Invalid datetime format.",
                List.of(new com.example.graph.web.problem.ProblemFieldError("at", "Invalid datetime format.")));
        }
    }

    private record TimeSlice(String requested, OffsetDateTime resolved) {
    }
}
