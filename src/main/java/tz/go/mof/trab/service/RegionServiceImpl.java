package tz.go.mof.trab.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.models.Region;
import tz.go.mof.trab.repositories.RegionRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;


@Service
public class RegionServiceImpl implements RegionService {

	private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);

	Response<Region> response = new Response<Region>();

	ListResponse<Region> listResponse = new ListResponse<Region>();

	@Autowired
	RegionRepository regionRepository;
	
	@Autowired
	LoggedUser loggedUser;

	@Override
	public ListResponse<Region> listAllRegions() {

// TODO Auto-generated method stub
		try {

			List<Region> regions= regionRepository.findAll();
			if(regions.size() >0 ) {
				listResponse.setData(regions);
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setStatus(false);
			}else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
			}

		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Failed to retrieve  regions with error : {} ",e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setStatus(false);
		}
		return listResponse;

	}
	@Override
	public Response<Region> getRegionByCode(String regionCode) {
		 // TODO Auto-generated method stub
        try {

            Optional<Region> council = regionRepository.findByCode(regionCode);
            if (council.isPresent()) {
                response.setData(council.get());
                response.setCode(ResponseCode.SUCCESS);
                response.setStatus(false);
            } else {
                response.setData(null);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setStatus(false);
            }

        } catch (Exception e) {
            // TODO: handle exception
            logger.error("Failed to retrieve  councils by region code with error : {} ", e);
            response.setData(null);
            response.setCode(ResponseCode.FAILURE);
            response.setStatus(false);
        }
        return response;
	}

}
