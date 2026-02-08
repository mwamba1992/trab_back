package tz.go.mof.trab.service;

import tz.go.mof.trab.dto.bill.AppellantDto;
import tz.go.mof.trab.models.Appellant;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface AppellantService {
    Fees findById(String revenueId);

    ListResponse<Appellant> findAllAppellants();

    Response<Appellant> getOneAppellant(String appellantId);

    Response<Appellant>  saveAppellant(AppellantDto appellantDto);

    Response<Appellant>  editAppellant(AppellantDto appellantDto, String appellantId);

    Response<Appellant> deleteAppellant(String appellantId);

    Response<Appellant> changeAppellantStatus(String appellantId, boolean status);

    Appellant findOrCreateByTin(String tinNumber, String name, String email, String phone, String natOfBus);
}
