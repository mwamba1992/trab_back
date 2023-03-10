package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.GfsDto;
import tz.go.mof.trab.models.Gfs;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface GfsService {


    public Gfs findById(String gfsId);

    public ListResponse<Gfs> findAllGfs();

    public Response<Gfs> getOneGfs(String gsfId);

    public Response<Gfs>  saveGfs(GfsDto gfsDto);

    public Response<Gfs>  editGfs(GfsDto gfsDto, String gsfId);

    public Response<Gfs> deleteGfs(String gsfId);

    public Gfs findByGfsCode(String gfsCode);


}
