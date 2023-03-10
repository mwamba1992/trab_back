package tz.go.mof.trab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import tz.go.mof.trab.dto.permission.PermissionCategoryXmlDto;
import tz.go.mof.trab.dto.permission.PermissionModel;
import tz.go.mof.trab.dto.permission.PermissionWrapperDto;
import tz.go.mof.trab.dto.permission.PermissionXmlDto;
import tz.go.mof.trab.dto.user.*;
import tz.go.mof.trab.models.*;
import tz.go.mof.trab.models.Currency;
import tz.go.mof.trab.repositories.*;
import tz.go.mof.trab.service.RegionService;


@Component
public class Initializer implements ApplicationRunner {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private  AppealsRepository appealsRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private SystemUserRepository userAccountRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UaaRepository clientDetailsRepository;

    @Autowired
	private PermissionRepository permissionRepository;

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private GfsRepository gfsRepository;

    @Value("${spring.load.data}")
    private Boolean loadDataOpt;

    @Autowired
    GlobalMethods globalMethods;

    @Autowired
    TaxTypeRepository taxTypeRepository;

    @Autowired
    ApplicationStatusTrendRepository applicationStatusTrendRepository;

    @Autowired
    RespondentRepository respondentRepository;

    @Autowired
    AppealantRepository appealantRepository;


    @Autowired
    ApplicationRegisterRepository applicationRegisterRepository;

    @Autowired
    AdressRepository adressRepository;


    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    AppealStatusTrendRepository appealStatusTrendRepository;

    @Autowired
    YearlyCasesRepository yearlyCasesRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    RegionService regionService;

    @Autowired
    SystemUserRepository systemUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    // Load ClientDetails
    @PostConstruct
    protected void initializeClientDetails() {
        if(loadDataOpt) {

            System.out.println("############# Start Initialize initialize ClientDetails  #####################");
            ClientDetailWrapperDto clientDetailWrapperDto = null;

            try {
                InputStream inputStream = new ClassPathResource("settings/clientDetails.xml").getInputStream();
                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(ClientDetailWrapperDto.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                clientDetailWrapperDto = (ClientDetailWrapperDto) jaxbUnmarshaller.unmarshal(inputStream);

                if (clientDetailWrapperDto != null) {

                    List<ClientDetailXmlDto> clientDetails = clientDetailWrapperDto.getListOauthClients();
                    for (ClientDetailXmlDto clientDetailXmlDto : clientDetails) {

                        // Prepare insertion data
                        OauthClientDetail newOauthClientDetail = new OauthClientDetail();
                        newOauthClientDetail.setAccessTokenValidity(clientDetailXmlDto.getAccessTokenValidity());
                        newOauthClientDetail.setAuthorities(clientDetailXmlDto.getAuthorities());
                        newOauthClientDetail.setAuthorizedGrantTypes(clientDetailXmlDto.getAuthorizedGrantTypes());
                        newOauthClientDetail.setAutoapprove("true");
                        newOauthClientDetail.setClientId(clientDetailXmlDto.getClientId());
                        newOauthClientDetail
                                .setClientSecret(bCryptPasswordEncoder.encode(clientDetailXmlDto.getClientSecret()));
                        newOauthClientDetail.setScope(clientDetailXmlDto.getScope());
                        newOauthClientDetail.setResourceIds(clientDetailXmlDto.getResourceId().trim());
                        newOauthClientDetail.setRefreshTokenValidity(clientDetailXmlDto.getAccessTokenValidity());

                        if (clientDetailsRepository.findByClientId(clientDetailXmlDto.getClientId()).isPresent() == false) {
                            OauthClientDetail savedOauthClientDetail = clientDetailsRepository.save(newOauthClientDetail);
                            if (savedOauthClientDetail == null) {
                                System.out.println("OauthClientDetail failed to be created : " + clientDetailXmlDto.getClientId());
                            }

                        } else {
                            System.out.println(" This OauthClientDetail already exists: " + clientDetailXmlDto.getClientId());
                        }
                    }

                } else {
                    System.out.println(" ##############  No OauthClientDetail found  on xml file ###############");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            System.out.println("############# End Initialize OauthClientDetail #####################");
        }

    }


    @PostConstruct
    protected void initializeUser() {

        if(loadDataOpt) {
            UserWrapperDto userWrapperDto = null;

            try {
                InputStream inputStream = new ClassPathResource("settings/users.xml").getInputStream();
                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(UserWrapperDto.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                userWrapperDto = (UserWrapperDto) jaxbUnmarshaller.unmarshal(inputStream);

                if (userWrapperDto != null) {

                    List<UserXmlDto> user = userWrapperDto.getListOfUsers();
                    for (UserXmlDto userXmlDto : user) {

                        // Prepare insertion data
                        SystemUser newUser = new SystemUser();
                        newUser.setEnabled(true);
                        newUser.setAccountNonExpired(true);
                        newUser.setAccountNonLocked(true);
                        newUser.setCredentialsNonExpired(true);
                        newUser.setUsername(userXmlDto.getEmail());
                        newUser.setEmail(userXmlDto.getEmail());
                        newUser.setName(userXmlDto.getName());
                        newUser.setPassword(bCryptPasswordEncoder.encode(userXmlDto.getPassword()));
                        newUser.setCreatedBy("ADMIN");
                        newUser.setRecordCreatedDate(new Date());


                        if (userAccountRepository.findByUsername(userXmlDto.getEmail()) == null) {
                            SystemUser savedUser = userAccountRepository.save(newUser);
                            if (savedUser == null) {
                                logger.info("User failed to be created : " + userXmlDto.getEmail());
                            }

                        } else {
                            logger.info(" This User already exists: " + userXmlDto.getEmail());
                        }
                    }

                } else {
                    logger.info(" ##############  No User  found  on xml file ###############");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            logger.info("############# End Initialize User  #####################");
        }
    }


    @PostConstruct
    protected void initializePermission() {
        if(loadDataOpt) {

            logger.info("############# Start Initialize  Permissions  #####################");

            PermissionWrapperDto permissionWrapperDto = null;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


            List<PermissionModel> list = new ArrayList<PermissionModel>();

            try {
                InputStream inputStream = new ClassPathResource("settings/permision.xml").getInputStream();


                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(PermissionWrapperDto.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                permissionWrapperDto = (PermissionWrapperDto) jaxbUnmarshaller.unmarshal(inputStream);

                if (permissionWrapperDto != null) {

                    List<PermissionCategoryXmlDto> permissionCategory = permissionWrapperDto.getListOfPermissionCategory();

                    for (PermissionCategoryXmlDto permissionCategoryObj : permissionCategory) {

                        PermissionModel permissionModel = new PermissionModel();
                        permissionModel.setCategoryName(permissionCategoryObj.getName());
                        permissionModel.setActive(permissionCategoryObj.getActive());

                        List<PermissionXmlDto> permissions = permissionCategoryObj.getPermissions();


                        if (permissions != null) {
                            for (PermissionXmlDto permissionObj : permissions) {
                                logger.info("###### Permission #####" + permissionCategoryObj.toString());
                                if (permissionRepository.findByDisplayName(permissionObj.getDisplayName()) != null) {

                                } else {
                                    Permission permission = new Permission();
                                    permission.setActive(permissionCategoryObj.getActive());
                                    permission.setDisplayName(permissionObj.getDisplayName());
                                    permission.setServiceName(permissionObj.getServiceName());
                                    permission.setPermissionCategory("NONE");
                                    permissionRepository.save(permission);
                                }
                            }
                        }
                        list.add(permissionModel);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Load fees
    @PostConstruct
    protected void loadingFees() {

        if (loadDataOpt) {
            logger.info("############# Start Initialize Fees #####################");
            Fiis wardWrapperDto = null;

            try {
                InputStream inputStream = new ClassPathResource("settings/fees.xml").getInputStream();
                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(Fiis.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                wardWrapperDto = (Fiis) jaxbUnmarshaller.unmarshal(inputStream);

                if (wardWrapperDto != null) {

                    List<Fii> fees = wardWrapperDto.getFiis();
                    for (Fii fee : fees) {

                        Fees fees1 = new tz.go.mof.trab.models.Fees();

                        logger.info("## gfs id #### " + fee.getCode());
                        fees1.setId(String.valueOf(Math.random()));
                        fees1.setActive(true);
                        fees1.setCreatedBy("SYSTEM INITIALIZED");
                        fees1.setAction("1");
                        fees1.setCreatedAt(LocalDateTime.now());
                        fees1.setAmount(new BigDecimal(fee.getAmount()));
                        fees1.setRevenueName(fee.getDesc());
                        fees1.setGfs(gfsRepository.findByGfsCodeAndActiveTrueAndDeletedFalse(fee.getCode()));
                        fees1.setId(fee.getId());


                        Optional<tz.go.mof.trab.models.Fees> optionalFees= feesRepository.findById(fee.getId());

                        if(optionalFees.isPresent() ==false) {
                            feesRepository.save(fees1);
                        }
                    }

                } else {
                    logger.warn(" ##############  No Fees Found on File ###############");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("############# Error on Loading Fees #####################", e);
            }
            logger.info("############# End Initialize Fees #####################");

        } else {

        }
    }

    //Load areas
    @PostConstruct
    protected void anzishaMikoa() {

        if (loadDataOpt) {
            logger.info("############# Start Initialize Administrative Areas #####################");
            Mikoa wardWrapperDto = null;

            try {
                InputStream inputStream = new ClassPathResource("settings/mikoa.xml").getInputStream();
                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(Mikoa.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                wardWrapperDto = (Mikoa) jaxbUnmarshaller.unmarshal(inputStream);

                if (wardWrapperDto != null) {

                    List<Mkoa> areas = wardWrapperDto.getMikoa();
                    for (Mkoa mkoa : areas) {
                        Region newRegion = new Region();
                        newRegion.setCode(mkoa.getKifupi());
                        newRegion.setName(mkoa.getJina());

                        Optional<Region> optRegion= regionRepository.findByCode(mkoa.getKifupi());

                        if(optRegion.isPresent() ==false) {
                            regionRepository.save(newRegion);
                        }
                    }

                } else {
                    logger.warn(" ##############  No Administrative Area found found  on xml file ###############");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("############# Error on Administrative Area #####################", e);
            }
            logger.info("############# End Initialize Administrative Area #####################");

        } else {

        }
    }

    //@PostConstruct
    void  loadAppeals() {
        if(loadDataOpt) {
            try {
                File FileName = new File(".");
                File file = new File("/Users/amtz/gepg/new_gepg/income_tax.xlsx");
                InputStream ExcelFileToRead = new FileInputStream(file.getPath());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-M-yyyy");
                HashMap<Integer, ArrayList<String>> readable = globalMethods.readReconFile("/Users/amtz/gepg/new_gepg/income_tax.xlsx", Files.getFileExtension(file.getName()));

                int i = 0;
                for (Map.Entry<Integer, ArrayList<String>> entry : readable.entrySet()) {
                    //i++;
                    try {
                        Appeals appeals = new Appeals();

                        int key = entry.getKey();
                        ArrayList<String> value = entry.getValue();
                        if (key > 0) {
                            if (!value.get(0).isEmpty()) {
                                System.out.println(value.get(0));
                                System.out.println(value.get(15));
                                if (appealsRepository.findByAppealNoAndTaxType(value.get(0).trim(), "5836820f1b7a11ecb55697a34e7adb91") == null) {
                                    appeals.setAppealNo(value.get(0).trim());
                                    appeals.setTinNumber(value.get(3).trim());
                                    appeals.setAssNo(value.get(4).trim());
                                    appeals.setTax(taxTypeRepository.findById("5836820f1b7a11ecb55697a34e7adb91").get());
                                    appeals.setNatureOfAppeal(value.get(11).trim());

                                    if (value.get(10).trim().equals("NO DATE")) {
                                        appeals.setDateOfFilling(null);
                                    } else {
                                        if (value.get(10).trim().split("/").length > 1) {
                                            appeals.setDateOfFilling(sdf.parse(value.get(10).trim()));
                                        } else {
                                            appeals.setDateOfFilling(sdf2.parse(value.get(10).trim()));
                                        }
                                    }
                                    appeals.setAppellantName(value.get(8).trim());
                                    appeals.setNoticeNumber(value.get(1).trim());

                                    Set<AppealAmount> appealAmounts = new HashSet<>();

                                    if (Long.parseLong(value.get(12)) != 0) {
                                        AppealAmount appealAmount = new AppealAmount();
                                        appealAmount.setAmountOnDispute(BigDecimal.valueOf(Long.parseLong(value.get(12))));
                                        appealAmount.setCurrency(currencyRepository.findByCurrencyShortName("USD"));
                                        appealAmount.setCreatedBy("System Created");
                                        appealAmount.setCurrencyName("USD");

                                        appealAmounts.add(appealAmount);
                                    }


                                    if (Long.parseLong(value.get(13)) != 0) {
                                        AppealAmount appealAmount = new AppealAmount();
                                        appealAmount.setAmountOnDispute(BigDecimal.valueOf(Long.parseLong(value.get(13))));
                                        appealAmount.setCurrency(currencyRepository.findByCurrencyShortName("TZS"));
                                        appealAmount.setCreatedBy("System Created");
                                        appealAmount.setCurrencyName("TZS");

                                        appealAmounts.add(appealAmount);
                                    }

                                    appeals.setAppealAmount(appealAmounts);


                                    if (value.get(14).trim().equals("NO DATE")) {
                                        appeals.setDecidedDate(null);
                                    } else {
                                        if (value.get(14).trim().split("/").length > 1) {
                                            appeals.setDecidedDate(sdf.parse(value.get(14).trim()));
                                        } else {
                                            appeals.setDecidedDate(sdf2.parse(value.get(14).trim()));
                                        }
                                    }


                                    if (value.get(15).trim().equals("NO STATUS")) {
                                        appeals.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendNameIgnoreSpaces("NEW"));
                                    } else {
                                        appeals.setStatusTrend(appealStatusTrendRepository.findAppealStatusTrendByAppealStatusTrendNameIgnoreSpaces(value.get(15).trim()));
                                    }
                                    appeals.setRemarks(value.get(16).trim());
                                    appeals.setSummaryOfDecree(value.get(16).trim());
                                    appeals.setDecidedBy(value.get(17).trim());


                                    appeals.setAnnextors(null);
                                    appeals.setWitnessId(null);


                                    appealsRepository.save(appeals);
                                    System.out.println("saved");


                                    // printing all appeals
                                    //System.out.println(appeals.toString());

                                } else {
                                    System.out.println("Appeal already saved");
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //@PostConstruct
    void loadApplications(){
        if(loadDataOpt) {
            try {
                File FileName = new File(".");
                File file = new File("/Users/amtz/gepg/new_gepg/income_tax_empty_date_status_version2_xls.xlsx");
                InputStream ExcelFileToRead = new FileInputStream(file.getPath());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                HashMap<Integer, ArrayList<String>> readable = globalMethods.readReconFile("/Users/amtz/gepg/new_gepg/income_tax_empty_date_status_version2_xls.xlsx", Files.getFileExtension(file.getName()));
                for (Map.Entry<Integer, ArrayList<String>> entry : readable.entrySet()) {
                    try {
                        ApplicationRegister applicationRegister = new ApplicationRegister();
                        int key = entry.getKey();
                        ArrayList<String> value = entry.getValue();
                        if (key > 0) {
                            if (!value.get(0).isEmpty()) {
                                System.out.println("appeal id: " + value.get(0));
                                if (applicationRegisterRepository.findByapplicationNoAndTaxesId(value.get(0), "b45964aa1adb11ecab48f34a3e07660e") == null) {


                                    applicationRegister.setTaxes(taxTypeRepository.findById("b45964aa1adb11ecab48f34a3e07660e").get());
                                    applicationRegister.setApplicationNo(value.get(0));

                                    if (value.get(6).trim().equalsIgnoreCase("NO STATUS")) {
                                        applicationRegister.setStatusTrend(applicationStatusTrendRepository.findById("8a285cae9eea11edad5f2548855d1bf6").get());
                                    } else {
                                        System.out.println("STATUS: " + value.get(6).trim());
                                        applicationRegister.setStatusTrend(applicationStatusTrendRepository.findApplicationStatusTrendByApplicationStatusTrendNameIgnoreSpaces(value.get(6).trim()));
                                    }

                                    applicationRegister.setDateOfFilling(sdf.parse(value.get(3)));

                                    if (!value.get(5).trim().equalsIgnoreCase("NO DATE")) {
                                        applicationRegister.setDateOfDecision(sdf.parse(value.get(5)));
                                    } else {
                                        applicationRegister.setDateOfDecision(null);
                                    }
                                    applicationRegister.setRemarks(value.get(7));
                                    applicationRegister.setDesicionSummary(value.get(7).getBytes());
                                    applicationRegister.setDecideBy(value.get(8));
                                    applicationRegister.setNatureOfRequest(value.get(4));
                                    applicationRegister.setCreatedBy("SYSTEM UPLOADED");

                                    if (value.get(1).startsWith("COMM")) {
                                        System.out.println("inside application from COMM general");
                                        applicationRegister.setType("1");
                                        applicationRegister.setApplicant(appealantRepository.findById(Long.valueOf(1)).get());
                                        if (respondentRepository.findRespondentByName(value.get(2)) != null) {
                                            applicationRegister.setRespondent(respondentRepository.findRespondentByName(value.get(3)));
                                        } else {
                                            Respondent respondent = new Respondent();
                                            respondent.setName(value.get(2));
                                            respondent.setPhoneNumber("NONE");
                                            respondent.setEmailAdress("NONE");
                                            respondent.setTinNumber("NONE");
                                            respondent.setIncomeTaxFileNumber("NONE");
                                            respondent.setNatureOfBussiness("NONE");

                                            applicationRegister.setRespondent(respondentRepository.save(respondent));
                                            System.out.println("##### saving respondent for COMM general #####");
                                        }
                                    } else {
                                        applicationRegister.setType("2");
                                        applicationRegister.setRespondent(respondentRepository.findById(Long.valueOf(1)).get());

                                        if (appealantRepository.findAppealantByFirstName(value.get(1)) != null) {
                                            applicationRegister.setApplicant(appealantRepository.findAppealantByFirstName(value.get(1)));
                                        } else {
                                            Appellant appealant = new Appellant();
                                            appealant.setFirstName(value.get(1));
                                            appealant.setTinNumber("NONE");
                                            appealant.setPhoneNumber("NONE");
                                            appealant.setEmail("NONE");
                                            appealant.setIncomeTaxFileNumber("NONE");
                                            appealant.setCreatedDate(new Date());

                                            applicationRegister.setApplicant(appealantRepository.save(appealant));

                                        }

                                    }

                                    System.out.println("Region: " + value.get(0));
                                    Adress adress = new Adress();
                                    adress.setRegion(regionRepository.findByCode(value.get(0).split("\\.")[0].trim()).get());
                                    adress.setSlp("SLP");
                                    applicationRegister.setAdressId(adressRepository.save(adress));

                                    System.out.println("####### inside saving ###########");
                                    applicationRegisterRepository.save(applicationRegister);
                                } else {
                                    System.out.println("####### inside not saving ###########");
                                }
                                System.out.println(applicationRegister.toString());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    @PostConstruct
     void loadPreviousNotices(){

        if(false) {

            try {
                System.out.println("###### load notices ######");
                File FileName = new File(".");
                File file = new File("/Users/amtz/gepg/new_gepg/trab_live_keys/notices.xlsx");
                InputStream ExcelFileToRead = new FileInputStream(file.getPath());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
                HashMap<Integer, ArrayList<String>> readable = globalMethods.readReconFile("/Users/amtz/gepg/new_gepg/trab_live_keys/notices.xlsx", Files.getFileExtension(file.getName()));
                for (Map.Entry<Integer, ArrayList<String>> entry : readable.entrySet()) {
                    try {
                        Notice notice = new Notice();
                        int key = entry.getKey();
                        ArrayList<String> value = entry.getValue();
                        if (key > 0) {
                            if(noticeRepository.findBynoticeNo(value.get(0))==null) {

                                Adress adress = new Adress();
                                adress.setSlp("");
                                adress.setRegion(regionService.getRegionByCode(null).getData());

                                System.out.println(value.get(0));
                                System.out.println(value.get(1));
                                System.out.println(value.get(2));
                                System.out.println(value.get(3));
                                System.out.println(value.get(4));
                                notice.setNoticeNo(value.get(0));
                                notice.setDateOfTaxationDesicion(sdf.parse(value.get(2)));
                                notice.setDateOfServiceTaxationDesicion(sdf.parse(value.get(3)));
                                notice.setLoggedAt(sdf.parse(value.get(4)));
                                notice.setAppelantName(value.get(1));
                                notice.setBillId(billRepository.findById("1").get());
                                notice.setAdressId(adressRepository.save(adress));
                                notice.setSystemUser(systemUserRepository.findById("3083bacabb3a11eda50ad555443cfd51").get());
                                notice.setDes("1");
                                noticeRepository.save(notice);
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }


    @Override
    public void run(ApplicationArguments args) {
        // TODO Auto-generated method stub

    }

}
