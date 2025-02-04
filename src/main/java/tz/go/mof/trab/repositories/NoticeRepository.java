package tz.go.mof.trab.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.dto.NoticeListAppeal;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.models.Notice;

import java.util.Date;
import java.util.List;


@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "notices", path = "notices")
public interface NoticeRepository extends PagingAndSortingRepository<Notice, Long> {

	Notice findBynoticeNo(String noticeNo);

	Notice findTopByOrderByNoticeIdDesc();

	@Query(value = "SELECT nextval(notice_sequence)", nativeQuery = true)
	int getSequence();


	@Query(value = "select count(*) from Notice n 	WHERE n.billId.financialYear =:financialYear")
	int getNoticeCountFinancialYear(String financialYear);


	Page<Notice> findAllByLoggedAtBetween(Date startDate, Date endDate, Pageable pageable);


	@Query(value = "select n from Notice n where n.billId.billId =:billId")
	Notice findNoticeByBill(String billId);

	@Query(value = "SELECT * FROM trat_bkp.notice_list_appeal", nativeQuery = true)
	List<Object[]> findAllTratAppeals();


	@Query(value = "SELECT * FROM  trat_bkp.appeals where notice_id =:noticeId", nativeQuery = true)
	List<Object[]> findTratAppealByNoticeId(Long noticeId);


}
