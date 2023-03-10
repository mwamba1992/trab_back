package tz.go.mof.trab.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.GfsDto;
import tz.go.mof.trab.models.Gfs;
import tz.go.mof.trab.repositories.GfsRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;



@Service
@Transactional
public class GfsServiceImpl implements GfsService {

    @Autowired
    private LoggedUser loggedUser;

    private static final Logger logger = LoggerFactory.getLogger(GfsServiceImpl.class);


    Response<Gfs> response = new Response<Gfs>();

    ListResponse<Gfs> responseList = new ListResponse<Gfs>();

    @Autowired
    private GfsRepository gfsRepository;

    @Override
    public Gfs findById(String gsfId) {
        return gfsRepository.findById(gsfId).get();
    }

    @Override
    public ListResponse<Gfs> findAllGfs() {
        logger.info(loggedUser.getInfo().toString());
        List<Gfs> gfsList = gfsRepository.findByActiveTrue();
        if (gfsList.size() < 1) {
            responseList.setCode(ResponseCode.NO_RECORD_FOUND);
            responseList.setStatus(false);
            responseList.setData(null);
        } else {
            responseList.setCode(ResponseCode.SUCCESS);
            responseList.setStatus(true);
            responseList.setData(gfsList);
            responseList.setTotalElements(Long.valueOf(gfsList.size()));
        }
        return responseList;
    }

    @Override
    public Response<Gfs> getOneGfs(String gsfId) {
        gfsRepository.findById(gsfId).get();
        response.setCode(ResponseCode.SUCCESS);
        response.setData(gfsRepository.findById(gsfId).get());
        response.setDescription("SUCCESS");
        response.setStatus(true);
        return response;
    }

    @Override
    public Response<Gfs> saveGfs(GfsDto gfsDto) {
        try {
            if (gfsRepository.findByGfsCodeAndActiveTrueAndDeletedFalse(gfsDto.getGfsCode()) == null) {
                Gfs gfs = new Gfs();
                TrabHelper.copyNonNullProperties(gfsDto, gfs);

                response.setCode(ResponseCode.SUCCESS);
                gfs.setCreatedBy(loggedUser.getInfo().getName());
                gfs.setAction("1");
                response.setData(gfsRepository.save(gfs));
                response.setCode(ResponseCode.SUCCESS);
                response.setDescription("SUCCESS");
                response.setStatus(true);
            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Gfs Code Already Exists");
                response.setStatus(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("FAILURE");
            response.setStatus(false);

        }
        return response;
    }

    @Override
    public Response<Gfs> editGfs(GfsDto gfsDto, String gsfId) {
        try {
            if (gfsRepository.findById(gsfId).get() != null) {
                Gfs gfs = gfsRepository.findById(gsfId).get();
                TrabHelper.copyNonNullProperties(gfsDto, gfs);

                gfs.setUpdatedAt(LocalDateTime.now());
                gfs.setUpdatedBy(loggedUser.getInfo().getName());
                gfs.setAction("2");
                response.setCode(ResponseCode.SUCCESS);
                response.setData(gfsRepository.save(gfs));
                response.setDescription("SUCCESS");
                response.setStatus(true);

            } else {
                response.setCode(ResponseCode.FAILURE);
                response.setData(null);
                response.setDescription("Error! Updating Currency");
                response.setStatus(false);
            }

        } catch (Exception e) {
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Not Found");
            response.setStatus(false);
        }
        return response;
    }

    @Override
    public Response<Gfs> deleteGfs(String gsfId) {
        try {
            Gfs gfs = gfsRepository.findById(gsfId).get();
            gfs.setDeleted(true);
            gfs.setActive(false);
            gfs.setDeletedBy(loggedUser.getInfo().getName());
            gfs.setAction("3");
            gfs.setDeletedAt(LocalDateTime.now());
            gfsRepository.save(gfs);

            response.setData(gfs);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("SUCCESS");
            response.setStatus(true);

        }catch (Exception e){
            logger.error("#########"+ e.getMessage() + "############");
            response.setCode(ResponseCode.FAILURE);
            response.setData(null);
            response.setDescription("Currency! Could Not be Deleted");
            response.setStatus(false);
        }

        return response;
    }

    @Override
    public Gfs findByGfsCode(String gfsCode) {
        return gfsRepository.findGfsByGfsCode(gfsCode);
    }
}
