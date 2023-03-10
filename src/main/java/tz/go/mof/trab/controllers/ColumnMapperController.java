package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.payment.ColumnMapperDto;
import tz.go.mof.trab.models.ColumnMapper;
import tz.go.mof.trab.service.ColumnMapperService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

import javax.validation.Valid;



@Controller
@RequestMapping("/api/column-mapping")
public class ColumnMapperController {

    @Autowired
    private ColumnMapperService columnMapperService;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<ColumnMapper> getAllMappings() {
        return columnMapperService.findAll();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<ColumnMapper> createColumnMapping(@Valid @RequestBody ColumnMapperDto mapperDto) {
        return columnMapperService.saveMapping(mapperDto);
    }


    @PutMapping(path = "/{mapperId}", produces = "application/json")
    @ResponseBody
    public Response editColumnMapping(@PathVariable("mapperId") String mapperId, @Valid @RequestBody ColumnMapperDto mapperDto) {
        return  columnMapperService.editMapping(mapperDto, mapperId);
    }

    @DeleteMapping(path = "/{mapperId}", produces = "application/json")
    @ResponseBody
    public Response deleteColumnMapping(@PathVariable("mapperId") String mapperId) {
        return  columnMapperService.deleteMapper(mapperId);
    }

}
