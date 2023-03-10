package tz.go.mof.trab.service;


import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.AppellantDto;
import tz.go.mof.trab.models.Appellant;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

@Service
public class AppellantServiceImpl implements  AppellantService{
    @Override
    public Fees findById(String revenueId) {
        return null;
    }

    @Override
    public ListResponse<Appellant> findAllAppellants() {
        return null;
    }

    @Override
    public Response<Appellant> getOneAppellant(String appellantId) {
        return null;
    }

    @Override
    public Response<Appellant> saveAppellant(AppellantDto appellantDto) {
        return null;
    }

    @Override
    public Response<Appellant> editAppellant(AppellantDto appellantDto, String appellantId) {
        return null;
    }

    @Override
    public Response<Appellant> deleteAppellant(String appellantId) {
        return null;
    }

    @Override
    public Response<Appellant> changeAppellantStatus(String appellantId, boolean status) {
        return null;
    }
}
