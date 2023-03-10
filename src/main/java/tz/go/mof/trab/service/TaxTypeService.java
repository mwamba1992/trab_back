package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.GfsDto;
import tz.go.mof.trab.dto.bill.TaxTypeDto;
import tz.go.mof.trab.models.TaxType;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface TaxTypeService {


    public TaxType findById(String gfsId);

    public ListResponse<TaxType> findAllTaxTypes();

    public Response<TaxType> getOneTax(String taxId);

    public Response<TaxType>  saveTaxType(TaxTypeDto taxTypeDto);

    public Response<TaxType>  editTaxType(TaxTypeDto taxTypeDto, String gsfId);

    public Response<TaxType> deleteTaxType(String gsfId);

}
