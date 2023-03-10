package tz.go.mof.trab.service;


import tz.go.mof.trab.models.Region;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface RegionService {
	
	public ListResponse<Region> listAllRegions();

	public Response<Region> getRegionByCode(String regionCode);
}
