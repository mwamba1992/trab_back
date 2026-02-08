package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.AppellantDto;
import tz.go.mof.trab.models.Appellant;
import tz.go.mof.trab.service.AppellantService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/appellants")
public class AppellantController {

    @Autowired
    private AppellantService appellantService;

    @GetMapping
    public ListResponse<Appellant> getAllAppellants() {
        return appellantService.findAllAppellants();
    }

    @GetMapping("/{id}")
    public Response<Appellant> getAppellant(@PathVariable("id") String id) {
        return appellantService.getOneAppellant(id);
    }

    @PostMapping
    public Response<Appellant> createAppellant(@RequestBody AppellantDto dto) {
        return appellantService.saveAppellant(dto);
    }

    @PutMapping("/{id}")
    public Response<Appellant> updateAppellant(@PathVariable("id") String id, @RequestBody AppellantDto dto) {
        return appellantService.editAppellant(dto, id);
    }

    @DeleteMapping("/{id}")
    public Response<Appellant> deleteAppellant(@PathVariable("id") String id) {
        return appellantService.deleteAppellant(id);
    }
}
