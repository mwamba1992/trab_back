package tz.go.mof.trab.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.AppellantDto;
import tz.go.mof.trab.models.Appellant;
import tz.go.mof.trab.models.Fees;
import tz.go.mof.trab.repositories.AppealantRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppellantServiceImpl implements AppellantService {

    @Autowired
    private AppealantRepository appealantRepository;

    @Override
    public Fees findById(String revenueId) {
        return null;
    }

    @Override
    public ListResponse<Appellant> findAllAppellants() {
        ListResponse<Appellant> response = new ListResponse<>();
        List<Appellant> appellants = new ArrayList<>();
        appealantRepository.findAll().forEach(appellants::add);
        response.setStatus(true);
        response.setCode(ResponseCode.SUCCESS);
        response.setData(appellants);
        response.setTotalElements((long) appellants.size());
        return response;
    }

    @Override
    public Response<Appellant> getOneAppellant(String appellantId) {
        Response<Appellant> response = new Response<>();
        Optional<Appellant> appellant = appealantRepository.findById(Long.parseLong(appellantId));
        if (appellant.isPresent()) {
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(appellant.get());
        } else {
            response.setStatus(false);
            response.setCode(ResponseCode.NO_RECORD_FOUND);
            response.setDescription("Appellant not found");
        }
        return response;
    }

    @Override
    public Response<Appellant> saveAppellant(AppellantDto appellantDto) {
        Response<Appellant> response = new Response<>();
        try {
            Appellant appellant = new Appellant();
            appellant.setFirstName(appellantDto.getFirstName());
            appellant.setLastName(appellantDto.getLastName());
            appellant.setNatureOfBusiness(appellantDto.getNatureOfBusiness());
            appellant.setPhoneNumber(appellantDto.getPhoneNumber());
            appellant.setEmail(appellantDto.getEmail());
            appellant.setTinNumber(appellantDto.getTinNumber() != null ? appellantDto.getTinNumber() : "NONE");
            appellant.setIncomeTaxFileNumber(appellantDto.getIncomeTaxFileNumber() != null ? appellantDto.getIncomeTaxFileNumber() : "NONE");
            appellant.setVatNumber(appellantDto.getVatNumber() != null ? appellantDto.getVatNumber() : "NONE");
            appellant.setCreatedDate(new Date());

            Appellant saved = appealantRepository.save(appellant);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(saved);
            response.setDescription("Appellant saved successfully");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Failed to save appellant: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Appellant> editAppellant(AppellantDto appellantDto, String appellantId) {
        Response<Appellant> response = new Response<>();
        try {
            Optional<Appellant> existing = appealantRepository.findById(Long.parseLong(appellantId));
            if (!existing.isPresent()) {
                response.setStatus(false);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Appellant not found");
                return response;
            }
            Appellant appellant = existing.get();
            if (appellantDto.getFirstName() != null) appellant.setFirstName(appellantDto.getFirstName());
            if (appellantDto.getLastName() != null) appellant.setLastName(appellantDto.getLastName());
            if (appellantDto.getNatureOfBusiness() != null) appellant.setNatureOfBusiness(appellantDto.getNatureOfBusiness());
            if (appellantDto.getPhoneNumber() != null) appellant.setPhoneNumber(appellantDto.getPhoneNumber());
            if (appellantDto.getEmail() != null) appellant.setEmail(appellantDto.getEmail());
            if (appellantDto.getTinNumber() != null) appellant.setTinNumber(appellantDto.getTinNumber());
            if (appellantDto.getIncomeTaxFileNumber() != null) appellant.setIncomeTaxFileNumber(appellantDto.getIncomeTaxFileNumber());
            if (appellantDto.getVatNumber() != null) appellant.setVatNumber(appellantDto.getVatNumber());

            Appellant saved = appealantRepository.save(appellant);
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setData(saved);
            response.setDescription("Appellant updated successfully");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Failed to update appellant: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Appellant> deleteAppellant(String appellantId) {
        Response<Appellant> response = new Response<>();
        try {
            Optional<Appellant> existing = appealantRepository.findById(Long.parseLong(appellantId));
            if (!existing.isPresent()) {
                response.setStatus(false);
                response.setCode(ResponseCode.NO_RECORD_FOUND);
                response.setDescription("Appellant not found");
                return response;
            }
            appealantRepository.delete(existing.get());
            response.setStatus(true);
            response.setCode(ResponseCode.SUCCESS);
            response.setDescription("Appellant deleted successfully");
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(ResponseCode.FAILURE);
            response.setDescription("Failed to delete appellant: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Appellant> changeAppellantStatus(String appellantId, boolean status) {
        Response<Appellant> response = new Response<>();
        response.setStatus(false);
        response.setCode(ResponseCode.FAILURE);
        response.setDescription("Status change not supported");
        return response;
    }

    @Override
    public Appellant findOrCreateByTin(String tinNumber, String name, String email, String phone, String natOfBus) {
        // 1. Search by TIN first (unique identifier)
        if (tinNumber != null && !tinNumber.trim().isEmpty() && !"NONE".equalsIgnoreCase(tinNumber.trim())) {
            Optional<Appellant> byTin = appealantRepository.findByTinNumber(tinNumber.trim());
            if (byTin.isPresent()) {
                return byTin.get();
            }
        }

        // 2. Search by exact name match
        if (name != null && !name.trim().isEmpty()) {
            Appellant byName = appealantRepository.findByFirstNameIgnoreCase(name.trim());
            if (byName != null) {
                return byName;
            }
        }

        // 3. Not found — create new Appellant record
        Appellant appellant = new Appellant();
        appellant.setFirstName(name != null ? name.trim() : "UNKNOWN");
        appellant.setLastName("");
        appellant.setNatureOfBusiness(natOfBus != null ? natOfBus.trim() : "");
        appellant.setPhoneNumber(phone != null ? phone.trim() : "");
        appellant.setEmail(email != null ? email.trim() : "");
        appellant.setTinNumber(tinNumber != null && !tinNumber.trim().isEmpty() ? tinNumber.trim() : "NONE");
        appellant.setCreatedDate(new Date());

        return appealantRepository.save(appellant);
    }
}
