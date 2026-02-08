package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.migration.MigrationPhase1Result;
import tz.go.mof.trab.dto.migration.MigrationPhase2Result;
import tz.go.mof.trab.service.AppellantMigrationService;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/migration")
public class AppellantMigrationController {

    @Autowired
    private AppellantMigrationService migrationService;

    /**
     * Phase 1 PREVIEW: See what appellants will be created (dry run — no changes made)
     */
    @GetMapping("/extract-appellants/preview")
    public Response<MigrationPhase1Result> previewExtraction() {
        Response<MigrationPhase1Result> response = new Response<>();
        try {
            MigrationPhase1Result result = migrationService.extractAppellants(true);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(result);
            response.setDescription("Phase 1 preview completed");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Preview failed: " + e.getMessage());
        }
        return response;
    }

    /**
     * Phase 1 EXECUTE: Create Appellant records from appeal data
     */
    @PostMapping("/extract-appellants")
    public Response<MigrationPhase1Result> extractAppellants() {
        Response<MigrationPhase1Result> response = new Response<>();
        try {
            MigrationPhase1Result result = migrationService.extractAppellants(false);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(result);
            response.setDescription("Phase 1 extraction completed");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Extraction failed: " + e.getMessage());
        }
        return response;
    }

    /**
     * Phase 2 PREVIEW: See which appeals will be linked (dry run — no changes made)
     */
    @GetMapping("/link-appeals/preview")
    public Response<MigrationPhase2Result> previewLinking() {
        Response<MigrationPhase2Result> response = new Response<>();
        try {
            MigrationPhase2Result result = migrationService.linkAppeals(true);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(result);
            response.setDescription("Phase 2 preview completed");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Preview failed: " + e.getMessage());
        }
        return response;
    }

    /**
     * Phase 2 EXECUTE: Set appealant_id FK on each appeal
     */
    @PostMapping("/link-appeals")
    public Response<MigrationPhase2Result> linkAppeals() {
        Response<MigrationPhase2Result> response = new Response<>();
        try {
            MigrationPhase2Result result = migrationService.linkAppeals(false);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(result);
            response.setDescription("Phase 2 linking completed");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Linking failed: " + e.getMessage());
        }
        return response;
    }
}
