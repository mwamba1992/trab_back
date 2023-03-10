package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tz.go.mof.trab.models.Region;
import tz.go.mof.trab.service.RegionService;
import tz.go.mof.trab.utils.ListResponse;


@RestController
@RequestMapping(value="/applicant")
public class RegionController {

	@Autowired
	RegionService regionService;
	
	@RequestMapping(value = "/api/regions", method = RequestMethod.GET)
	public ListResponse<Region> getRegions() {
		return regionService.listAllRegions();
	}
}
