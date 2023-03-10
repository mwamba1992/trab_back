package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.GfsDto;
import tz.go.mof.trab.models.Gfs;
import tz.go.mof.trab.service.GfsService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/gfs")
public class GfsController {

    @Autowired
    private GfsService gfsService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ListResponse<Gfs> viewAllGfs(@RequestHeader HttpHeaders headers) {
        return gfsService.findAllGfs();
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public Response<Gfs> createGfs(@Valid @RequestBody GfsDto gfsDto, @RequestHeader HttpHeaders headers)
            throws Exception {

        return gfsService.saveGfs(gfsDto);
    }


    @GetMapping(path = "/{gfsId}", produces = "application/json")
    @ResponseBody
    public Response<Gfs> getOneGfs(@PathVariable("gfsId") String gfsId, @RequestHeader HttpHeaders headers) {
        return gfsService.getOneGfs(gfsId);
    }


    @PutMapping(path = "/{gfsId}", produces = "application/json")
    @ResponseBody
    public Response<Gfs> editGfs(@PathVariable("gfsId") String gfsId,
                                           @Valid @RequestBody GfsDto gfsDto, @RequestHeader HttpHeaders headers) {
        return gfsService.editGfs(gfsDto, gfsId);
    }


    @DeleteMapping(path = "/{gfsId}", produces = "application/json")
    @ResponseBody
    public Response<Gfs> deleteGfs(@PathVariable("gfsId") String gfsId, @RequestHeader HttpHeaders headers)
            throws Exception {

        return gfsService.deleteGfs(gfsId);

    }


}
